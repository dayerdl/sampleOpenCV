import org.opencv.core.*
import org.opencv.features2d.Features2d
import org.opencv.features2d.SimpleBlobDetector
import org.opencv.features2d.SimpleBlobDetector_Params
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Image
import javax.swing.*
import kotlin.random.Random
import kotlin.system.exitProcess


object ImageCircleDetector {

    private lateinit var srcGray: Mat
    private var frame: JFrame? = null
    private var imgSrcLabel: JLabel? = null
    private var imgContoursLabel: JLabel? = null
    private const val MAX_THRESHOLD = 255
    private const val threshold = 100
    private val rng: Random = Random(12345)

    @JvmStatic
    fun main(args: Array<String>) {
        //Compulsory -> Load Native libraries
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        println("Loaded OpenCV version " + Core.VERSION)
        srcGray = Mat()
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater { processImage() }
    }

    private fun processImage() {
//        val filename = "/Users/davidlopez.dayer/sampleOpenCV/src/image_with_circles.jpeg"
        val filename = "/Users/davidlopez.dayer/sampleOpenCV/src/dl.png"
        val src = Imgcodecs.imread(filename)
        if (src.empty()) {
            System.err.println("Cannot read image: $filename")
            exitProcess(0)
        }

        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY)
        //Imgproc.blur(srcGray, srcGray, Size(3.0, 3.0))

        frame = JFrame("Finding contours in your image demo")
        frame!!.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        // Set up the content pane.
        val img: Image = HighGui.toBufferedImage(src)

//        val blackImg = Mat.zeros(srcGray.size(), CvType.CV_8U)
//        imgContoursLabel = JLabel(ImageIcon(HighGui.toBufferedImage(blackImg)))


        val bilateral = Mat()
        Imgproc.bilateralFilter(srcGray, bilateral, 5, 175.0, 175.0)
//        val otsu = Mat()
//        Imgproc.threshold(bilateral, otsu,0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
        val edge = Mat()
        Imgproc.Canny(bilateral, edge, 75.0, 200.0)

        val contours = arrayListOf<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(edge, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        val contourList = arrayListOf<MatOfPoint2f>()
        val drawing = Mat.zeros(edge.size(), CvType.CV_8UC3)

        //contours
//        var i = 0
//        for (c in contours) {
//            val matcontour = MatOfPoint2f(*c.toArray())
//            val epsilon = 0.01 * Imgproc.arcLength(matcontour, true)
//            val approx = MatOfPoint2f()
//            Imgproc.approxPolyDP(matcontour, approx, epsilon, true)
//            val area = Imgproc.contourArea(matcontour)
//            if (approx.toArray().size in 9..29 && area > 30) {
//                contourList.add(matcontour)
//                val color = Scalar(rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble())
//                Imgproc.drawContours(drawing, contours, i, color, 2, Imgproc.LINE_8, hierarchy, 0, Point())
//                i++
//            }
//        }

        contours//contours//contours//contours//contours//contours//contours//contours

//        for (c in contours) {
//            val matcontour = MatOfPoint2f(*c.toArray())
//            val peri = Imgproc.arcLength(matcontour, true)
//            val approx = MatOfPoint2f()
//            val epsilon = 0.04 * peri
//            Imgproc.approxPolyDP(matcontour, approx, epsilon, true)
//            val area = Imgproc.contourArea(matcontour)
//
//            if (approx.toArray().size > 5 && area > 10 && area < 500 && matcontour.toArray().isNotEmpty()) {
//                val center = Point()
//                val radius = floatArrayOf()
//                Imgproc.minEnclosingCircle(approx, center, radius)
//                Imgproc.circle(drawing, center, radius[0].toInt(), Scalar(0.0, 0.0, 255.0), 1)
//            }
//        }
//        println("ZAXA Total of contours are ${contourList.size}")


        /// circles OpenCV ///
        val contoursPoly = arrayOfNulls<MatOfPoint2f>(contours.size)
        val centers = arrayOfNulls<Point>(contours.size)
        val radius = Array(contours.size) { FloatArray(1) }
        for (i in contours.indices) {
            contoursPoly[i] = MatOfPoint2f()
            Imgproc.approxPolyDP(MatOfPoint2f(*contours[i].toArray()), contoursPoly[i], 3.0, false)
            centers[i] = Point()
            Imgproc.minEnclosingCircle(contoursPoly[i], centers[i], radius[i])
        }
        val contoursPolyList: MutableList<MatOfPoint> = java.util.ArrayList(contoursPoly.size)
        for (poly in contoursPoly) {
            contoursPolyList.add(MatOfPoint(*poly!!.toArray()))
        }
        for (i in contours.indices) {
            val area = Imgproc.contourArea(contours[i])
            println("ZAXA area=$area, size=${contoursPoly[i]!!.toArray().size}")
            if (contoursPoly[i]!!.toArray().size in 1..90 && area > 10) {
                val color = Scalar(rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble())
                Imgproc.drawContours(drawing, contoursPolyList, i, color)
                Imgproc.circle(drawing, centers[i], radius[i][0].toInt(), color, 2)
            }
        }
        ////// circles OPenCV ////// circles OPenCV ////// circles OPenCV ////// circles OPenCV ////// circles OPenCV ////// circles OPenCV ///

        imgContoursLabel = JLabel(ImageIcon(HighGui.toBufferedImage(drawing)))
        addComponentsToPane(frame!!.contentPane, img, imgContoursLabel!!)

        frame!!.pack();
        frame!!.isVisible = true;
        //   detectCircles();
    }

    private fun Mat.simpleBlobDetectDraw(): Mat {
        val mat = Mat.zeros(this.size(), CvType.CV_8UC3)
        val output = Mat()
        val params = SimpleBlobDetector_Params()
        // Change thresholds
        params._minThreshold = 10f
        params._maxThreshold = 200f

        // Filter by Area.
        params._filterByArea = true
        params._minArea = 10f

        // Filter by Circularity
        params._filterByCircularity = true
        params._minCircularity = 0.1f

        // Filter by Convexity
//        params._filterByConvexity = true
//        params._minConvexity = 0.87f

        // Filter by Inertia
//        params._filterByInertia = true
//        params._minInertiaRatio = 0.01f

        val sbd = SimpleBlobDetector.create(params)
        val keyPoints = MatOfKeyPoint()
        sbd.detect(this, keyPoints)
        val color = Scalar(rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble())
        Features2d.drawKeypoints(mat, keyPoints, output, color, Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS)
        return mat
    }

    private fun detectContours() {
        val cannyOutput = Mat()
        Imgproc.Canny(srcGray, cannyOutput, threshold.toDouble(), (threshold * 2).toDouble())
        val contours: List<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        val drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3)
        for (i in contours.indices) {
            val color = Scalar(rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble())
            Imgproc.drawContours(drawing, contours, i, color, 2, Imgproc.LINE_8, hierarchy, 0, Point())
        }
        imgContoursLabel!!.icon = ImageIcon(HighGui.toBufferedImage(drawing))
        frame!!.repaint()
    }

    private fun detectCircles() {
        val cannyOutput = Mat()
        Imgproc.Canny(srcGray, cannyOutput, threshold.toDouble(), (threshold * 2).toDouble())
        val contours: List<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        val drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3)
        for (i in contours.indices) {
            val color = Scalar(rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble())
            Imgproc.drawContours(drawing, contours, i, color, 2, Imgproc.LINE_8, hierarchy, 0, Point())
        }
        imgContoursLabel!!.icon = ImageIcon(HighGui.toBufferedImage(drawing))
        frame!!.repaint()
    }

    private fun addComponentsToPane(pane: Container, img: Image, finalImage: JLabel) {
        if (pane.layout !is BorderLayout) {
            pane.add(JLabel("Container doesn't use BorderLayout!"))
            return
        }

        val imgPanel = JPanel()
        imgSrcLabel = JLabel(ImageIcon(img))
        imgPanel.add(imgSrcLabel)

        imgPanel.add(finalImage)
        pane.add(imgPanel, BorderLayout.CENTER)
    }
}