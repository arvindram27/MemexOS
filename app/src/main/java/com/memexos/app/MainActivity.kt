package com.memexos.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private lateinit var urlEditText: TextInputEditText
    private lateinit var toolbar: Toolbar
    private lateinit var fabRecord: FloatingActionButton
    private lateinit var fabSettings: FloatingActionButton
    private lateinit var progressOverlay: FrameLayout
    private lateinit var progressText: TextView
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        initializeViews()
        
        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        // Setup WebView
        setupWebView()
        
        // Setup URL input
        setupUrlInput()
        
        // Setup FABs
        setupFABs()
        
        // Load initial URL (can be changed to any default URL)
        webView.loadUrl("https://www.google.com")
    }
    
    private fun initializeViews() {
        webView = findViewById(R.id.webView)
        urlEditText = findViewById(R.id.urlEditText)
        toolbar = findViewById(R.id.toolbar)
        fabRecord = findViewById(R.id.fabRecord)
        fabSettings = findViewById(R.id.fabSettings)
        progressOverlay = findViewById(R.id.progressOverlay)
        progressText = findViewById(R.id.progressText)
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.setSupportZoom(true)
            
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // Update URL in the address bar
                    urlEditText.setText(url)
                }
            }
            
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    // You can add a progress bar here if needed
                }
            }
        }
    }
    
    private fun setupUrlInput() {
        urlEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO || 
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                loadUrl(urlEditText.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }
    
    private fun setupFABs() {
        fabRecord.setOnClickListener {
            // Start recording
            startRecording()
        }
        
        fabSettings.setOnClickListener {
            // Open settings
            // TODO: Implement settings activity
        }
    }
    
    private fun loadUrl(url: String) {
        var formattedUrl = url.trim()
        if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
            formattedUrl = "https://$formattedUrl"
        }
        webView.loadUrl(formattedUrl)
    }
    
    private fun startRecording() {
        // Show progress overlay
        showProgressOverlay("Recording...")
        
        // TODO: Implement actual recording logic with Whisper
        // For now, simulate recording with a delay
        webView.postDelayed({
            processRecording()
        }, 3000)
    }
    
    private fun processRecording() {
        // Update progress text
        progressText.text = "Processing speech..."
        
        // TODO: Implement actual speech processing with Whisper
        // For now, simulate processing with a delay
        webView.postDelayed({
            hideProgressOverlay()
            // TODO: Execute the processed command
        }, 2000)
    }
    
    private fun showProgressOverlay(message: String) {
        progressText.text = message
        progressOverlay.visibility = View.VISIBLE
    }
    
    private fun hideProgressOverlay() {
        progressOverlay.visibility = View.GONE
    }
    
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
