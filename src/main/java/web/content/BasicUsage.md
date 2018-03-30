# 1.2 Basic Usage

In this section, the basic mechanics of how to detect organoids in an image will be covered.
<br />
As briefly mentioned in [1.1 Introduction]($LOCAL/Introduction.md), there are 3 image components on the screen.
The leftmost one is the *input image*. This is where a picture of organoids is input to be scanned.
There are 3 ways to input an image into this image component:

* Navigate to the File menu in the toolbar, and then click on Open.
  You can also use the shortcut for this, which is Ctrl+O.
  A file explorer will pop up, and you can navigate to image that you want to use.
  Currently, PNG, JPG, BMP, and GIF formats are supported.
* Another way to input a file is my clicking on the first image component.
  This will also open a file explorer.
* The third way to input a file is by dragging an image file into the first image component.
  For example, in your system's file explorer, an image file can be dragged onto the first image component.

Once an image is input, we can see that there's some data already displayed above the image component.
* The first piece of data is the name of the image that's being displayed.
* The second is the resolution of the image, in pixels.
* The third is the resolution of the image, in micrometers (μm). The rate of pixels to micrometers can be changed. This is detailed in [1.5 Configuring]($LOCAL/Configuring.md).
* The fourth is the compression notifier. If the image is not a PNG image, this will display _Possible Compression_.
  If the image is a PNG image, this will display _No Compression_.
  Keep in mind that this is not guaranteed for PNG images-- compression is still possible, if a compressed image is converted to a PNG image.

Now that we understand this data, we can look underneath the first image component.
Here, there are three fields.
<br />
The first field is the blur factor. This is an amount used in the blurring algorithm.
This is used to blur the input image. The reason for blurring it is to smooth the image, so that there are less false positives.
There is not a solid way to find this value besides trial and error. Typically, this value is between 0.5 and 2.
<br />
The second field is the minimum size that an organoid can be, in micrometers.
When this value is too low, the detection algorithm is more prone to false positives.
<br />
The third field in the minimum distance between organoids, in micrometers.
No two organoids can have the distance between _centers_ lower than this value.
<br />
<br />
Once we have these values roughly configured, we can scan the input image by pressing the _Measure_ button.
This button will first blur the image based on the blur factor.
The blurred image will appear on the second image component.
<br />
Then, the blurred image will be input into the organoid-detecting algorithm.
The resulting image with appear in the third image component.
Congratulations! You have successfully scanned an image.
<br />
<br />
// TODO

--------

### [Table of Contents]($LOCAL/TableOfContents.md)
