# 1.2 Basic Usage

In this section of the manual, the basic mechanics of how to detect organoids in an image will be covered.
<br />
There is a good amount of material to be covered here, so this section is divided into 5 subsections:

### <a href="#" data-goto="part-1">1.2.1</a> Selecting an Image
### <a href="#" data-goto="part-2">1.2.2</a> Input Analytics
### <a href="#" data-goto="part-3">1.2.3</a> Input Parameters
### <a href="#" data-goto="part-4">1.2.4</a> Generating Results
### <a href="#" data-goto="part-5">1.2.5</a> Output Analytics

<br />

### <a id="part-1"></a>Selecting an Image

As briefly mentioned in [1.1 Introduction]($LOCAL/Introduction.md), there are 3 image components on the screen.
The leftmost one is the *input image*. This is where a picture of organoids is input to be scanned.
There are 3 ways to input an image into this image component:

* Navigate to the File menu in the toolbar, and then click on Open.
  You can also use the shortcut for this, which is Ctrl+O.
  A file explorer will pop up, and you can navigate to that image that you want to use.
  Currently, PNG, JPG, BMP, and GIF formats are supported.
* Another way to input a file is by clicking on the first image component.
  This will also open a file explorer.
* The third way to input a file is by dragging an image file into the first image component.
  For example, in your system's file explorer, an image file can be dragged onto the first image component.

### <a id="part-2"></a>Input Analytics

Once an image is input, we can see that there's some data already displayed above the image component.
* The first piece of data is the name of the image that's being displayed.
* The second is the resolution of the image, in pixels.
* The third is the resolution of the image, in micrometers (μm). The rate of pixels to micrometers can be changed. This is detailed in [1.5 Configuring]($LOCAL/Configuring.md).
* The third is the greyscale notifier. If the image is not greyscale, meaning that there is color in the image, this will display _Not Greyscale_.
  This is not a bad thing, but it will take a fraction of a second longer to make the image greyscale.
  If the image is greyscale, then the notifier will display _Greyscale_.
* The fourth is the compression notifier. If the image is not a PNG image, this will display _Possible Compression_.
  If the image is a PNG image, this will display _No Compression_.
  Keep in mind that this is not guaranteed for PNG images-- compression is still possible, if a compressed image is converted to a PNG image.

### <a id="part-3"></a>Input Parameters

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
No two organoids can have the distance between their _centers_ lower than this value.

### <a id="part-4"></a>Generating Results

Once we have the parameters roughly configured, we can scan the input image by pressing the _Measure_ button.
This button will first blur the image based on the blur factor.
The blurred image will appear on the second image component.
If the input image is not greyscale, then the greyscale version of the image will be blurred.
<br />
Then, the blurred image will be input into the organoid-detecting algorithm.
The resulting image with appear in the third image component.
Congratulations! You have successfully scanned an image.
Once an image is generated, you may notice that not all detections are completely accurate.
Editing the detections in the output is discussed in [1.3 Modifying Data]($LOCAL/ModifyingData.md).

### <a id="part-5"></a>Output Analytics

After the output is generated, there is some data displayed above the output image component, similar to the input.
* The first piece of data is how long it took for the detection to take place in seconds.
* The second is the total amount of organoids that have been detected.
* The third is the average diameter of the organoids, in micrometers.

### Next up: [1.3 Modifying Data]($LOCAL/ModifyingData.md)

--------

### [Table of Contents]($LOCAL/TableOfContents.md)
