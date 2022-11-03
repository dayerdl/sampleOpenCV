import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc

object MatUtils {

    fun rotateImage(img: Mat, dst: Mat) {
        val rotPoint = Point(
            img.cols() / 2.0,
            img.rows() / 2.0
        )
        val rotMat = Imgproc.getRotationMatrix2D(
            rotPoint, 180.0, 1.0
        )

        Imgproc.warpAffine(img, dst, rotMat, img.size(),
            Imgproc.WARP_INVERSE_MAP);
    }
}