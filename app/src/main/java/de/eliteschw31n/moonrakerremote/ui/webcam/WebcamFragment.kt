package de.eliteschw31n.moonrakerremote.ui.webcam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.eliteschw31n.moonrakerremote.R

class WebcamFragment : Fragment() {

    private lateinit var webcamViewModel: WebcamViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        webcamViewModel =
                ViewModelProvider(this).get(WebcamViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_webcam, container, false)
        val textView: TextView = root.findViewById(R.id.text_webcam)
        webcamViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}