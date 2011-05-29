#!/usr/bin/env python

import matplotlib.pyplot as plt
import Image
import ImageFilter
import sys

# pritaikyti vidurkinimo, sobelio operatorius. 
# Atlikti histogramos normalizavima
# Atlikti atspindziu (oclusion) panaikinima.

"""
  Linear filtering operators -> 
    involve weighted combinations of pixels in small neighborhoods.
"""

class _SOBELX(ImageFilter.BuiltinFilter):
    name = "Sobel_X"
    filterargs = (3,3), 0, 0, (-1, 0, 1, -2, 0, 2, -1, 0, 1)

class _SOBELY(ImageFilter.BuiltinFilter):
    name = "Sobel_Y"
    filterargs = (3,3), 0, 0, (1, 2, 1, 0, 0, 0, -1, -2, -1)

class _AVERAGE(ImageFilter.BuiltinFilter):
    name = "Averaging"
    filterargs = (3,3), 16, 0, (1, 2, 1, 2, 4, 2, 1, 2, 1)

""" Histogram equalization """

def equalize(h, size):
    """ 
      histogram equalization.
      h - histogram of an image
    """
    xy, table = im.size[0]*im.size[1], []

    cdf = reduce(cdf_func, h, [])
    min_cdf = min(cdf)
    
    band_width = 256
    for band in range(0, len(h), band_width):
        for i in range(band_width):
            val = cdf[i] 
            table.append(round( (val - min_cdf) * 255 / (xy - min_cdf)) )

    return table

def cdf_func(l, el):
    if len(l) > 0: 
        value = el + l[-1]
    else:
        value = el

    l.append(value)
    return l

def plot_histogram(h, h1):
    plt.plot(h, 'b-', h1, 'r-')

def equalize_test(im):
    h = im.histogram()

    lut = equalize(im.histogram(), im.size)
    im = im.point(lut)

    plot_histogram(h, im.histogram())

    plt.show()
    im.show()

if __name__ == '__main__':
    im = Image.open(sys.argv[1])

    #im = im.filter(_AVERAGE)
    #im = im.filter(_SOBELX)
    #im = im.filter(_SOBELX)

    im.show()
    equalize_test(im)
