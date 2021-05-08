package xyz.getclear.android.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import xyz.getclear.android.R
import xyz.getclear.android.data.BASE_URL
import xyz.getclear.android.databinding.FragmentWebhookBinding
import xyz.getclear.android.details.WebHookDetailsFragmentArgs
import xyz.getclear.android.common.FullScreenBottomSheetDialog

class WebHookDetailsFragment : FullScreenBottomSheetDialog() {
    private val args: WebHookDetailsFragmentArgs by navArgs()
    private val token by lazy { args.token }

    companion object {
        const val DATA_STRING = "{\n" +
                "\t\"id\" : \"%s\",\n" +
                "\t\"amount\" : %.2f\n" +
                "}"
        const val DATA_STRING1 = "{\n" +
                "\t\"id\" : \"%s\",\n" +
                "  \"data\": [\n" +
                "\t{\n" +
                "\t\t\"date\": \"2015-01-30T16:44:14.224092Z\",\n" +
                "\t\"amount\" : 100.50\n" +
                "\t},\n" +
                "\t\t\t{\n" +
                "\t\t\"date\": \"2014-01-30T16:44:14.224092Z\",\n" +
                "\t\"amount\" : 25.10\n" +
                "\t}\t\n" +
                "\t\t]\n" +
                "}"
    }

    private lateinit var binding: FragmentWebhookBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebhookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtHookUrl.text = """${BASE_URL}push/"""
        binding.txtHookData.text = DATA_STRING1.format(token)
        binding.btnHookShare.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getEmailContent())
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_instructions)))
        }
    }

    private fun getEmailContent(): String {
        val output = StringBuffer()
        output.append(resources.getString(R.string.webhook_instructions))
        output.append("\n\n Url: \n")
        output.append("""${BASE_URL}push/""")
        output.append("\n\n Data: \n")
        output.append(DATA_STRING.format(token,100.12))
        output.append("\n\n")
        output.append(resources.getString(R.string.webhook_note))
        return output.toString()
    }

}