#!/usr/bin/env python

import matplotlib.pyplot as plt
import Image
import ImageFilter

source = "akis.tiff"
#source = "test.jpg"

# pritaikyti vidurkinimo, sobelio operatorius. 
# Atlikti histogramos normalizavima
# Atlikti atspindziu (oclusion) panaikinima.

"""
  Linear filtering operators -> 
    involve weighted combinations of pixels in small neighborhoods.
"""

class _SOBEL(ImageFilter.BuiltinFilter):
    name = "Sobel"
    filterargs = (3,3), 0, 0, (-1, -2, -1, 0, 0, 0, +1, +2, +1)

class _AVERAGE(ImageFilter.BuiltinFilter):
    name = "Averaging"
    filterargs = (3,3), 16, 0, (1, 2, 1, 2, 4, 2, 1, 2, 1)

def equalize(h, x, y):
    """ 
      histogram equalization.
    """
    xy, table = x*y, []
    cdf = reduce(cdf_func, h, [0])
    min_cdf = min(cdf)

    for band in range(0, len(h), 256):
        for i in range(256):
            table.append(int(round((cdf[i] - min_cdf) * 255 / (xy - min_cdf))))
    return table

def cdf_func(l, el):
    value = el + l[-1]
    l.append(value)
    return l

def plot_histogram(h, h1):
    plt.plot(h, 'b-', h1, 'r-')

def equalize_test(im):
    x, y = im.size
    h = im.histogram()

    lut = equalize(im.histogram(), x, y)

    im = im.point(lut)
    plot_histogram(h, im.histogram())
    plt.show()
    im.show()

if __name__ == '__main__':
    im = Image.open(source)
    #im = im.filter(_AVERAGE)
    im = im.filter(_SOBEL)
    #equalize_test(im)
    im.show()
