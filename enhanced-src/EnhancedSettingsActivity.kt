package dev.multiview.mesh
import android.app.Activity
import android.os.Bundle
import android.widget.*
import java.io.File
import java.net.HttpURLConnection
import java.security.MessageDigest
class EnhancedSettingsActivity:Activity(){
 private lateinit var status:TextView; private lateinit var progress:ProgressBar
 private val prefs by lazy{getSharedPreferences("enhanced",MODE_PRIVATE)}
 private val model by lazy{File(filesDir,"models/enhanced-model-v1.json")}
 override fun onCreate(b:Bundle?){super.onCreate(b);title="Enhanced reconstruction";val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(32,32,32,32)};val toggle=Switch(this).apply{text="Enhanced reconstruction (not LRM)";isChecked=prefs.getBoolean("enabled",false);setOnCheckedChangeListener{_,v->prefs.edit().putBoolean("enabled",v).apply()}};status=TextView(this);progress=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal).apply{max=100};val get=Button(this).apply{text="Download public package";setOnClickListener{download()}};val del=Button(this).apply{text="Delete package";setOnClickListener{model.delete();show("Local package deleted")}};box.addView(toggle);box.addView(TextView(this).apply{text="Verified package enables 3-frame temporal masks, a 96³ native visual hull, denser output and 2048px texture. Processing is local after download."});box.addView(progress);box.addView(status);box.addView(get);box.addView(del);setContentView(box);show(if(model.exists())"Verified package ready" else "Package not downloaded")}
 private fun show(s:String){status.text=s}
 private fun download(){Thread{try{model.parentFile?.mkdirs();val tmp=File(model.path+".part");val c=java.net.URL(PACKAGE_URL).openConnection() as HttpURLConnection;c.connectTimeout=15000;c.readTimeout=30000;c.connect();if(c.responseCode !in 200..299)error("HTTP ${c.responseCode}");val total=c.contentLengthLong;val md=MessageDigest.getInstance("SHA-256");c.inputStream.use{i->tmp.outputStream().use{o->val buf=ByteArray(32768);var done=0L;while(true){val n=i.read(buf);if(n<0)break;o.write(buf,0,n);md.update(buf,0,n);done+=n;runOnUiThread{progress.progress=if(total>0)(done*100/total).toInt() else 0;show("Downloading ${progress.progress}%")}}}};val got=md.digest().joinToString(""){"%02x".format(it)};if(got!=PACKAGE_SHA){tmp.delete();error("SHA-256 mismatch")};if(!tmp.renameTo(model))error("Cannot store package");runOnUiThread{progress.progress=100;show("Verified; local enhanced mode ready")}}catch(e:Exception){runOnUiThread{show("Download failed: ${e.message}")}}}.start()}
 companion object{const val PACKAGE_URL="https://github.com/ujujkm337/multiview-mesh-apk/releases/download/enhanced-model-v1/enhanced-model-v1.json";const val PACKAGE_SHA="6982f4432a9ac9a5acf8901d27b023696789f0d7d63be0ff3e80e0e2edfd3545"}
}
