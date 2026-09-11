package dev.multiview.mesh
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
class CameraActivity:AppCompatActivity(){
 private var imageCapture:ImageCapture?=null
 private lateinit var angle:CaptureView
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContentView(R.layout.activity_camera);angle=CaptureView.valueOf(intent.getStringExtra(EXTRA_VIEW)!!);findViewById<android.widget.TextView>(R.id.guide).text="Capture ${angle.label}\nCenter the object and keep distance consistent";startCamera();findViewById<android.widget.Button>(R.id.capture).setOnClickListener{captureSeries(0,if(EnhancedReconstruction.enabled(this))3 else 1)}}
 private fun startCamera(){val future=ProcessCameraProvider.getInstance(this);future.addListener({val provider=future.get();val preview=Preview.Builder().build().also{it.setSurfaceProvider(findViewById<androidx.camera.view.PreviewView>(R.id.preview).surfaceProvider)};imageCapture=ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).setJpegQuality(if(EnhancedReconstruction.enabled(this))100 else 95).build();provider.unbindAll();provider.bindToLifecycle(this,CameraSelector.DEFAULT_BACK_CAMERA,preview,imageCapture)},ContextCompat.getMainExecutor(this))}
 private fun captureSeries(index:Int,count:Int){val dir=File(filesDir,"captures").apply{mkdirs()};val name=if(index==0)"${angle.ordinal}.jpg" else "${angle.ordinal}_$index.jpg";val file=File(dir,name);imageCapture?.takePicture(ImageCapture.OutputFileOptions.Builder(file).build(),ContextCompat.getMainExecutor(this),object:ImageCapture.OnImageSavedCallback{override fun onImageSaved(r:ImageCapture.OutputFileResults){if(index+1<count){findViewById<android.widget.TextView>(R.id.guide).text="Enhanced burst ${index+1}/$count";captureSeries(index+1,count)}else{setResult(RESULT_OK,intent.putExtra(EXTRA_VIEW,angle.name));finish()}}override fun onError(e:ImageCaptureException){findViewById<android.widget.TextView>(R.id.guide).text="Capture failed: ${e.message}"}})}
 companion object{const val EXTRA_VIEW="capture_view"}
}
