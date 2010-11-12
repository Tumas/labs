#!/usr/bin/ruby

require 'opengl'

include Gl
include Glu
include Glut

$WIDTH = 800
$HEIGHT = 600

LEFT_POINT_ID = 21
CENTER_POINT_ID = 22
RIGHT_POINT_ID = 23

$p1 = [1.4, 0, -0.9]
$p2 = [0.9, 0, -1.4]

# camera controls
$x_camera_angle = -20 
$y_camera_angle = 20 
$scale_factor = 1
$lift_y = 0

# mouse interaction 
$adjusted
$mouse_x = 0
$mouse_y = 0
$x_direction = 1

def init
  glEnable GL_DEPTH_TEST
end

def get_sphere_coords(x, y)
  temp = (x**2) + (y**2) + 1
  #puts "INPUT: (#{x}, #{y}) OUT: (#{x/temp}, #{(temp-1)/temp}, #{y/temp})"

  [x/temp, (temp-1)/temp, y/temp]
end

def test_coords(c)
  p c[0]**2 + c[2]**2 + c[1]** 2 - c[1] 
end

def draw_line(mode)
  glBegin GL_LINES
    glVertex3fv $p1
    glVertex3fv $p2
  glEnd

  glPushMatrix
    glColor3f 0.9, 0.8, 0.2
    glPointSize 8

    glLoadName LEFT_POINT_ID if mode == GL_SELECT
    glBegin GL_POINTS
      glVertex3fv $p1
    glEnd

    glLoadName RIGHT_POINT_ID if mode == GL_SELECT
    glBegin GL_POINTS
      glVertex3fv $p2
    glEnd

    glLoadName CENTER_POINT_ID if mode == GL_SELECT
    glBegin GL_POINTS
      glVertex3f ($p1.first + $p2.first) / 2, 0, ($p1.last + $p2.last) / 2
    glEnd
  glPopMatrix
end

def draw_scene(mode)
  # enter drawing mode
  glMatrixMode GL_MODELVIEW
  glLoadIdentity
  glTranslatef -0.75, 0.7, -5

  # camera controls
  glRotatef $x_camera_angle, 1, 0, 0
  glRotatef $y_camera_angle, 0, 1, 0
  glScalef $scale_factor, $scale_factor, $scale_factor
  glTranslatef 0, $lift_y, 0

  glLineWidth 1
  glPushMatrix
    glTranslatef 0, +0.5, 0
    glRotatef 90, 1, 0, 0 
    glColor3f 0.3, 0.5, 0.7

    glutWireSphere(0.5, 16, 16)
  glPopMatrix

  glPushMatrix
    glColor3f 0.5, 0.2, 1 
    glBegin GL_LINES
      glVertex3f 0, 0, -5
      glVertex3f 0, 0, 2

      glVertex3f 0, 0, 0
      glVertex3f 0, 2, 0

      glVertex3f -2, 0, 0
      glVertex3f 2, 0, 0
    glEnd

    glColor3f 1, 1, 1
    glLineWidth 2

    draw_line mode
  glPopMatrix

  # line projection 
  glColor3f 0.9, 0.8, 0.2
  glBegin GL_LINES
    glVertex3fv get_sphere_coords $p1.first, $p1.last
    glVertex3fv get_sphere_coords $p2.first, $p2.last 
  glEnd

=begin
  test_coords get_sphere_coords($p1.first, $p1.last)
  test_coords get_sphere_coords($p2.first, $p2.last)
=end
end

display = lambda do
  glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
  draw_scene GL_RENDER
  glutSwapBuffers
end

reshape = lambda do |w, h|
  $WIDTH, $HEIGHT = w, h
  glViewport 0, 0, w, h
  glMatrixMode GL_PROJECTION
  glLoadIdentity
  gluPerspective 45.0, w/h, 0.1, 100.0
end

keyboard = lambda do |key, x, y|
  case key 
    when ?\e
      exit 0
    when ?a
      $y_camera_angle -= 1
    when ?s
      $x_camera_angle += 1
    when ?d
      $y_camera_angle += 1
    when ?w
      $x_camera_angle -= 1
    when ?+
      $scale_factor += 0.02
    when ?-
      $scale_factor -= 0.02
    when ?q
      $lift_y += 0.1
    when ?e
      $lift_y -= 0.1
  end
end

mouse_motion = lambda do |new_x, new_y|
  if $adjusted == CENTER_POINT_ID
    temp = new_x - $mouse_x

    if temp > 0
      $x_direction = 1
    elsif temp < 0
      $x_direction = -1
    end

    if $x_direction > 0
      $p1[0] += 0.01
      $p2[0] += 0.01
      $p1[2] += 0.01
      $p2[2] += 0.01
    else
      $p1[0] -= 0.01
      $p2[0] -= 0.01
      $p1[2] -= 0.01
      $p2[2] -= 0.01
    end
  end

  if $adjusted == LEFT_POINT_ID
  end

  if $adjusted == RIGHT_POINT_ID
  end

  $mouse_x = new_x
  $mouse_y = new_y
end

pick_points = lambda do |button, state, x, y|
  if button == GLUT_LEFT_BUTTON
    viewport = glGetDoublev GL_VIEWPORT

    select_buf = glSelectBuffer 64
    glRenderMode GL_SELECT

    glInitNames
    glPushName 0

    glMatrixMode GL_PROJECTION
    glPushMatrix
      glLoadIdentity
      gluPickMatrix x, viewport[3] - y,
        8, 8, viewport
      gluPerspective 45.0, $WIDTH/$HEIGHT, 0.1, 100.0
      glMatrixMode GL_MODELVIEW
      glutSwapBuffers

      draw_scene GL_SELECT
      glMatrixMode GL_PROJECTION
    glPopMatrix
    glFlush

    hits = glRenderMode GL_RENDER
    process_hits hits, select_buf, x, y
  end
end

def process_hits(hits, buffer, x, y)
  ptr = buffer.unpack("I*")
  p = 0
  for i in 0 ... hits 	# for each hit
    names = ptr[p]      # number of names 
    p += 3              # skip min and max z values

    for j in 0...names  # for each name
      name = ptr[p]

      if name == CENTER_POINT_ID
        $adjusted = CENTER_POINT_ID
      elsif name == RIGHT_POINT_ID
        $adjusted = RIGHT_POINT_ID
      elsif name == LEFT_POINT_ID
        $adjusted == LEFT_POINT_ID
      end

      p += 1 
    end
  end
end

idle = lambda do
  glutPostRedisplay
end

glutInit()
glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH)
glutInitWindowSize($WIDTH, $HEIGHT)
glutInitWindowPosition(0, 0)
glutCreateWindow("Chaos Theory lab 3: Line and its projection on Riemann's sphere") 

init()

glutDisplayFunc(display)
glutReshapeFunc(reshape)
glutKeyboardFunc(keyboard)
glutIdleFunc(idle)

glutMouseFunc(pick_points)
glutMotionFunc(mouse_motion)

glutMainLoop
