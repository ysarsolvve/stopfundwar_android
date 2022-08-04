package sarzhane.e.stopfundwar_android.util

import android.content.Context
import androidx.annotation.NonNull
import org.tensorflow.lite.support.common.internal.SupportPreconditions
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


@NonNull
@Throws(IOException::class)
fun loadMappedFile(@NonNull context: Context, @NonNull filePath: String): MappedByteBuffer {
    SupportPreconditions.checkNotNull<Any>(context, "Context should not be null.")
    SupportPreconditions.checkNotNull(filePath, "File path cannot be null.")
    val file = File( filePath)
    val var9: MappedByteBuffer
    try {
        val inputStream = FileInputStream(file)
        var9 = try {
            val fileChannel: FileChannel = inputStream.channel
            fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
        } catch (var12: Throwable) {
            try {
                inputStream.close()
            } catch (var11: Throwable) {
                var12.addSuppressed(var11)
            }
            throw var12
        }
        inputStream.close()
    } catch (var13: Throwable) {
        throw var13
    }
    return var9
}