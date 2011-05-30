#!/usr/bin/env python

import matplotlib.pyplot as plt
import Image
import ImageFilter
import sys

from collections import deque

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

def flood_fill_iterative(image_data, x, y, target_range, rep_value, max_x, max_y):
    queue = deque()

    if not within(image_data[x, y], target_range): return
    queue.append((x, y))

    while True:
      if len(queue) == 0: break
        
      posx, posy = queue.popleft()
      if within(image_data[posx, posy], target_range): 
          wx = posx
          ex = posx

          while wx > 0 and within(image_data[wx, posy], target_range): wx -= 1
          while ex < max_x and within(image_data[ex, posy], target_range): ex += 1

          for nx in range(wx, ex): 
              image_data[nx, posy] = rep_value
              if posy > 0 and within(image_data[nx, posy-1], target_range): queue.append((nx, posy-1))
              if posy < max_y-1 and within(image_data[nx, posy+1], target_range): queue.append((nx, posy+1))

def within(val, ranges):
    return val >= min(ranges) and val <= max(ranges)

if __name__ == '__main__':
    im = Image.open(sys.argv[1])
    im.show()

    #im = im.filter(_AVERAGE)
    #im = im.filter(_SOBELX)
    #im = im.filter(_SOBELY)

    #equalize_test(im)

    im_data = im.load()

    x = 120
    y = 120

    #x = 291
    #y = 252

    flood_fill_iterative(im.load(), x, y, [245, 255], 0, im.size[0], im.size[1]) 
    im.show()
