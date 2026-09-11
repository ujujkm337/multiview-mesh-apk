package dev.multiview.mesh
import android.content.Context
object EnhancedReconstruction{
 init{System.loadLibrary("enhanced_visual_hull")}
 fun enabled(c:Context)=c.getSharedPreferences("enhanced",0).getBoolean("enabled",false)&&java.io.File(c.filesDir,"models/enhanced-model-v1.json").exists()
 fun temporalConsensus(masks:List<ByteArray>):ByteArray{require(masks.size>=3);return ByteArray(masks[0].size){i->if(masks.count{(it[i].toInt() and 255)>127}>=2)255.toByte() else 0}}
 external fun nativeVisualHull(masks:Array<ByteArray>,width:Int,height:Int,views:Int,grid:Int,smoothing:Int):FloatArray
 fun reconstruct(masks:Array<ByteArray>,w:Int,h:Int)=nativeVisualHull(masks,w,h,masks.size,96,4)
}
