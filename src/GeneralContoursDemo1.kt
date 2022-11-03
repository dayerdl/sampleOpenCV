import org.opencv.core.*
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Image
import java.util.*
import javax.swing.*

internal class GeneralContours1(args: Array<String>) {
    private val srcGray = Mat()
    private val frame: JFrame
    private var imgSrcLabel: JLabel? = null
    private var imgContoursLabel: JLabel? = null
    private var threshold = 100
    private val rng = Random(12345)

    init {
//        val filename = "/Users/davidlopez.dayer/sampleOpenCV/src/image_with_circles.jpeg"
        val filename = "/Users/davidlopez.dayer/sampleOpenCV/src/dl2.png"
        val src = Imgcodecs.imread(filename)
        if (src.empty()) {
            System.err.println("Cannot read image: $filename")
            System.exit(0)
        }
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.blur(srcGray, srcGray, Size(3.0, 3.0))
        // Create and set up the window.
        frame = JFrame("Creating Bounding boxes and circles for contours demo")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        // Set up the content pane.
        val img = HighGui.toBufferedImage(src)
        addComponentsToPane(frame.contentPane, img)
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack()
        frame.isVisible = true
        update()
    }

    private fun addComponentsToPane(pane: Container, img: Image) {
        if (pane.layout !is BorderLayout) {
            pane.add(JLabel("Container doesn't use BorderLayout!"))
            return
        }
        val sliderPanel = JPanel()
        sliderPanel.layout = BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS)
        sliderPanel.add(JLabel("Canny threshold: "))
        val slider = JSlider(0, MAX_THRESHOLD, threshold)
        slider.majorTickSpacing = 20
        slider.minorTickSpacing = 10
        slider.paintTicks = true
        slider.paintLabels = true
        slider.addChangeListener { e ->
            val source = e.source as JSlider
            threshold = source.value
            update()
        }
        sliderPanel.add(slider)
        pane.add(sliderPanel, BorderLayout.PAGE_START)
        val imgPanel = JPanel()
        imgSrcLabel = JLabel(ImageIcon(img))
        imgPanel.add(imgSrcLabel)
        val blackImg = Mat.zeros(srcGray.size(), CvType.CV_8U)
        imgContoursLabel = JLabel(ImageIcon(HighGui.toBufferedImage(blackImg)))
        imgPanel.add(imgContoursLabel)
        pane.add(imgPanel, BorderLayout.CENTER)
    }

    private fun update() {
        val cannyOutput = Mat()
        Imgproc.Canny(srcGray, cannyOutput, threshold.toDouble(), (threshold * 2).toDouble())
        val contours: List<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        val contoursPoly = arrayOfNulls<MatOfPoint2f>(contours.size)
        val boundRect = arrayOfNulls<Rect>(contours.size)
        val centers = arrayOfNulls<Point>(contours.size)
        val radius = Array(contours.size) { FloatArray(1) }
        for (i in contours.indices) {
            contoursPoly[i] = MatOfPoint2f()
            Imgproc.approxPolyDP(MatOfPoint2f(*contours[i].toArray()), contoursPoly[i], 3.0, true)
            boundRect[i] = Imgproc.boundingRect(MatOfPoint(*contoursPoly[i]!!.toArray()))
            centers[i] = Point()
            Imgproc.minEnclosingCircle(contoursPoly[i], centers[i], radius[i])
        }
        val drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3)
        val contoursPolyList: MutableList<MatOfPoint> = ArrayList(contoursPoly.size)
        for (poly in contoursPoly) {
            contoursPolyList.add(MatOfPoint(*poly!!.toArray()))
        }
        for (i in contours.indices) {
            val color = Scalar(rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble(), rng.nextInt(256).toDouble())
            Imgproc.drawContours(drawing, contoursPolyList, i, color)
            Imgproc.rectangle(drawing, boundRect[i]!!.tl(), boundRect[i]!!.br(), color, 2)
            Imgproc.circle(drawing, centers[i], radius[i][0].toInt(), color, 2)
        }
        imgContoursLabel!!.icon = ImageIcon(HighGui.toBufferedImage(drawing))
        frame.repaint()
    }

    companion object {
        private const val MAX_THRESHOLD = 255
    }
}

object GeneralContoursDemo1 {
    @JvmStatic
    fun main(args: Array<String>) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater { GeneralContours1(args) }
    }
}