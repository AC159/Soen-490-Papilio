package com.soen490chrysalis.papilio.view.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.BuildConfig
import com.soen490chrysalis.papilio.databinding.FragmentChatBinding
import com.soen490chrysalis.papilio.viewModel.UserChatViewModel
import com.soen490chrysalis.papilio.viewModel.factories.UserChatViewModelFactory
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory


class ChatFragment : Fragment()
{
    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var userChatViewModel : UserChatViewModel

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // Fetch the user token for the user to be able to use the chat feature
        val factory = UserChatViewModelFactory()
        userChatViewModel = ViewModelProvider(this, factory)[UserChatViewModel::class.java]
        userChatViewModel.getNewChatTokenForUser(FirebaseAuth.getInstance().currentUser!!.uid)

        userChatViewModel.userChatToken.observe(viewLifecycleOwner) {
            val chatToken = it

            // We can only setup the chat client once the user has received his chat token from the backend

            // Step 1 - Set up the OfflinePlugin for offline storage
            val offlinePluginFactory = StreamOfflinePluginFactory(
                config = Config(
                    backgroundSyncEnabled = true,
                    userPresence = true,
                    persistenceEnabled = true,
                    uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
                ),
                appContext = activity!!.applicationContext,
            )

            // Step 2 - Set up the client for API calls with the plugin for offline storage
            val client = ChatClient.Builder(BuildConfig.STREAM_CHAT_API_KEY, activity!!.applicationContext)
                    .withPlugin(offlinePluginFactory)
                    .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
                    .build()

            // Step 3 - Authenticate and connect the user
            val user = FirebaseAuth.getInstance().currentUser?.let {
                User(
                    id = it.uid,
                    name = "Some display name", // todo: Set display name and image url
                    image = "https://bit.ly/2TIt8NR"
                )
            }

            if (user != null)
            {
                client.connectUser(
                    user = user,
                    token = chatToken
                ).enqueue()

                // Step 4 - Set the channel list filter and order
                // This can be read as requiring only channels whose "type" is "messaging" AND
                // whose "members" include our "user.id"
                val filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", listOf(user.id))
                )
                val viewModelFactory =
                    ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT)
                val viewModel : ChannelListViewModel by viewModels { viewModelFactory }

                // Step 5 - Connect the ChannelListViewModel to the ChannelListView, loose
                //          coupling makes it easy to customize
                viewModel.bindView(binding.channelListView, this)
                binding.channelListView.setChannelItemClickListener { _channel ->
                    // Start channel activity
                    startActivity(
                        ChannelActivity.newIntent(
                            activity!!.applicationContext,
                            _channel
                        )
                    )
                }
            }
        }
    }
}