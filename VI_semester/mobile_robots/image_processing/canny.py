#!/usr/bin/env python

import sys
import math
import Image
import ImageFilter

"""
  Simplified Canny edge detector : works only with greyscale images
"""

class _GAUSSIAN(ImageFilter.BuiltinFilter):
    name   = "Gaussian"
    matrix = [
              2, 4,  5,  4, 2,
              4, 9, 12,  9, 4,
              5, 12,15, 12, 5,
              4, 9, 12,  9, 4,
              2, 4,  5,  4, 2
             ] 
    filterargs = (5,5), 159, 0, tuple(matrix)

class _SOBELX(ImageFilter.BuiltinFilter):
    name = "Sobel_X"
    filterargs = (3,3), 0, 0, (-1, 0, 1, -2, 0, 2, -1, 0, 1)

class _SOBELY(ImageFilter.BuiltinFilter):
    name = "Sobel_Y"
    filterargs = (3,3), 0, 0, (1, 2, 1, 0, 0, 0, -1, -2, -1)

def canny_filter(image):
    """ process image with canny edge detector """

    size = image.size
    im = Image.new("L", size)
    im.paste(image)

    # step 1: noise reduction with gaussian filter
    im = im.filter(_GAUSSIAN)

    # step 2: calculate grandient and get binary image
    gradient   = _find_intensity_gradient(im)
    im = _nonmaximum_suppression(gradient, size)

    return im


def _find_intensity_gradient(image):
    """ Find intensity gradient of an image """

    #imx = image.filter(_SOBELX).load()
    #imy = image.filter(_SOBELY).load()

    src = image.load()

    offset = [(-1, -1), (0, -1), (1, -1),
              (-1, 0), (0, 0), (1, 0),
              (-1, 1), (0, 1), (1, 1)]

    sobel_x = [-1, 0, 1, -2, 0, 2, -1,  0, 1]
    sobel_y = [ 1, 2, 1,  0, 0, 0, -1, -2, -1]

    intensity_greadient = {}

    for y in range(im.size[1]):
        for x in range(im.size[0]):
            ver, hor = 0, 0

            for i in range(len(offset)):
                coord = offset[i]
                xx, yy = x + coord[0], y + coord[1]

                if (0 <= xx < image.size[0] and 0 <= yy < image.size[1]):
                    hor += src[xx, yy] * sobel_x[i] 
                    ver += src[xx, yy] * sobel_y[i] 

            a, g = 0, math.sqrt(ver**2 + hor**2)
            if hor != 0: a = _angle_round(math.atan(ver / hor))

            intensity_greadient[x, y] = (g, a)

    return intensity_greadient

def _angle_round(angle):
    """ Round angle into one of four directions """

    if -22.5 < angle <= 22.5 or 157.5 < angle <= 202.5:
        return 0
    elif 22.5 < angle <= 67.5 or -157.5 < angle <= -112.5:
        return 45
    elif 67.5 < angle <= 112.5 or -112.5 < angle <= -67.5:
        return 90
    else:
        return 135

def _nonmaximum_suppression(intensity_greadient, size):
    """ get binary image according to intesity gradient """
    bin_img  = Image.new("L", size)
    bin_data = bin_img.load()

    for y in range(size[1]):
        for x in range(size[0]):
            g, angle = intensity_greadient[x, y]

            if angle == 0:
                if (x > 0 and (x < size[0]-1) and 
                    g > intensity_greadient[x - 1, y][0] and
                    g > intensity_greadient[x + 1, y][0]):

                    bin_data[x, y] = g 
            elif angle == 45:
                if (y > 0 and (y < size[1]-1) and x > 0 and (x < size[0]-1) and  
                    g > intensity_greadient[x - 1, y + 1][0] and
                    g > intensity_greadient[x + 1, y - 1][0]):

                    bin_data[x, y] = g 
            elif angle == 90:
                if (y > 0 and (y < size[1]-1) and 
                    g > intensity_greadient[x, y - 1][0] and
                    g > intensity_greadient[x, y + 1][0]):

                    bin_data[x, y] = g  
            else:
                # nw and se
                if (y > 0 and (y < size[1]-1) and x > 0 and (x < size[0]-1) and  
                    g > intensity_greadient[x - 1, y - 1][0] and
                    g > intensity_greadient[x + 1, y + 1][0]):

                    bin_data[x, y] = g 

    return bin_img

if __name__ == '__main__':
    im = Image.open(sys.argv[1])
    im.show()

    canny_filter(im).show()
