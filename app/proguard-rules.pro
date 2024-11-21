# Suppress warnings for missing classes
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# Preserve OkHttp classes
-keep class okhttp3.** { *; }

# Preserve BouncyCastle classes
-keep class org.bouncycastle.** { *; }

# Preserve Conscrypt classes
-keep class org.conscrypt.** { *; }

# Preserve OpenJSSE classes
-keep class org.openjsse.** { *; }

# Optional: If you use WebView with JavaScript
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#    public *;
# }

# Preserve source file and line number info for better debugging
-keepattributes SourceFile,LineNumberTable

# Optional: Hide original source file name
# -renamesourcefileattribute SourceFile
