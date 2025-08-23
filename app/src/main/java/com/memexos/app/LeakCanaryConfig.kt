package com.memexos.app

import android.app.Application
import android.webkit.WebView
import leakcanary.AppWatcher
import leakcanary.LeakCanary
import leakcanary.ObjectWatcher

/**
 * Configuration for LeakCanary to monitor memory leaks specific to MemexOS.
 * 
 * This configuration includes:
 * - Custom monitoring for JNI contexts (WhisperService)
 * - WebView lifecycle monitoring
 * - Custom heap dump analysis settings
 */
object LeakCanaryConfig {

    /**
     * Initialize LeakCanary with custom configuration.
     * Should be called from Application.onCreate() in debug builds only.
     */
    fun initialize(application: Application) {
        LeakCanary.config = LeakCanary.config.copy(
            // Retain heap dumps for analysis
            retainedVisibleThreshold = 3,
            
            // Configure heap dump analysis
            dumpHeap = true,
            dumpHeapWhenDebugging = true,
            
            // Custom analysis settings for our specific use case
            referenceMatchers = LeakCanary.config.referenceMatchers + listOf(
                // Ignore known Android framework leaks that we can't control
                ignoringInstanceField("android.view.inputmethod.InputMethodManager", "mCurRootView"),
                ignoringInstanceField("android.view.inputmethod.InputMethodManager", "mServedView"),
                
                // Ignore WebView related framework leaks (common in Android)
                ignoringInstanceField("android.webkit.WebView", "mWebViewCore"),
                ignoringStaticField("com.android.webview.chromium.WebViewChromium", "sInstance")
            )
        )
        
        // Setup custom object watchers
        setupCustomWatchers()
    }

    private fun setupCustomWatchers() {
        // Watch for WhisperService context pointer leaks
        AppWatcher.objectWatcher.watch(
            watchedObject = Any(), // This would be replaced with actual WhisperService instances
            description = "WhisperService JNI context monitoring"
        )
    }

    /**
     * Watch a WhisperService instance for potential JNI context leaks.
     * Call this whenever a WhisperService is created.
     */
    fun watchWhisperService(whisperService: Any, description: String = "WhisperService instance") {
        AppWatcher.objectWatcher.watch(whisperService, description)
    }

    /**
     * Watch a WebView instance for memory leaks.
     * Call this when WebView is destroyed or activity is finishing.
     */
    fun watchWebView(webView: WebView, description: String = "WebView instance") {
        AppWatcher.objectWatcher.watch(webView, description)
    }

    /**
     * Monitor native memory allocation.
     * This should be called when JNI contexts are created/destroyed.
     */
    fun logNativeMemoryAllocation(operation: String, contextPtr: Long) {
        if (BuildConfig.DEBUG) {
            println("LeakCanary: Native memory $operation - Context pointer: 0x${contextPtr.toString(16)}")
        }
    }

    /**
     * Helper function to ignore specific instance fields that are known to cause false positives.
     */
    private fun ignoringInstanceField(className: String, fieldName: String) =
        leakcanary.ReferenceMatcher.IgnoredReferenceMatcher(
            pattern = leakcanary.ReferencePattern.instanceField(className, fieldName)
        )

    /**
     * Helper function to ignore specific static fields that are known to cause false positives.
     */
    private fun ignoringStaticField(className: String, fieldName: String) =
        leakcanary.ReferenceMatcher.IgnoredReferenceMatcher(
            pattern = leakcanary.ReferencePattern.staticField(className, fieldName)
        )
}
