#!/usr/bin/env python
#Position analysis code for problem 1 of homework 3

import numpy as np
import matplotlib.pyplot as plt

def dist(p1, p2):
  dx = p1[0] - p2[0]
  dy = p1[1] - p2[1]
  return np.sqrt(dx*dx + dy*dy)

def trans(p, r, t):
  x = p[0] + r*np.cos(t)
  y = p[1] + r*np.sin(t)
  return [x, y]

#returns angle across from a
def cosine_law(a, b, c):
  return np.arccos((b*b + c*c - a*a)/(2*b*c))

def angle(p1, p2):
  dx = (p2[0] - p1[0])
  dy = (p2[1] - p1[1])
  return np.arctan2(dy, dx)

O2 = [6, 2]
O3 = [0, 0]
O4 = [3.6000, 8]
Ai = [3.5000, 2.0000]
Bi = [-2.4, 2.2]
Ci = [-7.8500, 3.1000]
Di = [-1.3000, 9.5000]
Ei = [-10.0000, -5.9000]
a = dist(O2, O3)
b = dist(Ai, O2)
c = dist(Ai, Bi)
d = dist(Bi, O3)
e = dist(Di, O4) 
f = dist(Ci, Bi) 
g = dist(Ci, Ai) 
h = dist(Ci, Di)
i = dist(Di, Ei)
j = dist(Ci, Ei)
rho = cosine_law(g,f,c)
eta = cosine_law(i,h,j)
t1 = angle(O3,O2)

scale = (1.0/2.5)*2
print "a=" + str(a*scale)
print "b=" + str(b*scale)
print "c=" + str(c*scale)
print "d=" + str(d*scale)
print "e=" + str(e*scale)
print "f=" + str(f*scale)
print "g=" + str(g*scale)
print "h=" + str(h*scale)
print "i=" + str(i*scale)
print "j=" + str(j*scale)
print "O2=" + str(O2[0]*scale) + ", "  + str(O2[1]*scale)
print "O4=" + str(O4[0]*scale) + ", "  + str(O4[1]*scale)
print "A=" + str(Ai[0]*scale) + ", "  + str(Ai[1]*scale)
print "B=" + str(Bi[0]*scale) + ", "  + str(Bi[1]*scale)
print "C=" + str(Ci[0]*scale) + ", "  + str(Ci[1]*scale)
print "D=" + str(Di[0]*scale) + ", "  + str(Di[1]*scale)
print "E=" + str(Ei[0]*scale) + ", "  + str(Ei[1]*scale)

def analyzePosition(t2):  
  global a
  global b
  global c
  global d
  global e
  global f
  global g
  global h
  global i
  global j
  global rho
  global eta
  global t1
  
  A = trans(O2, b, t2)
  l1 = dist(A, O3)
  alpha = cosine_law(b,l1,a)
  beta = cosine_law(c,l1,d)
  if ((t2 - t1) < np.pi) and ((t2 - t1) > 0):
    t3 = t1 + (beta + alpha)
  else:
    t3 = t1 + (beta - alpha)
  B = trans(O3, d, t3)
  phi = angle(B, A)
  psi = angle(B, O4)
  l2 = dist(B, O4)
  gamma = rho + phi
  C = trans(B, f, gamma)
  l3 = dist(C, O4)
  epsilon = angle(O4, C)
  delta = cosine_law(h,e,l3)
  t4 = epsilon - delta
  D = trans(O4,e,t4)
  lambd = angle(C,D)
  t5 = lambd-eta
  E = trans(C,j,t5)
  return [A, B, C, D, E]

def close(val1, val2):
  if (abs(val1-val2) < 0.0001):
    return True
  else:
    print "err " + str(abs(val1-val2))
    return False
  
def checkPositions(t2, A,B,C,D,E):
  if not close(a, dist(O2, O3)): print "a is bad for " + str(t2)
  if not close(b, dist(A, O2)): print "b is bad for " + str(t2)
  if not close(c, dist(A, B)): print "c is bad for " + str(t2)
  if not close(d, dist(B, O3)): print "d is bad for " + str(t2)
  if not close(e, dist(D, O4)): print "e is bad for " + str(t2)
  if not close(f, dist(C, B)): print "f is bad for " + str(t2)
  if not close(g, dist(C, A)): print "g is bad for " + str(t2)
  if not close(h, dist(C, D)): print "h is bad for " + str(t2)
  if not close(i, dist(D, E)): print "i is bad for " + str(t2)
  if not close(j,dist(C, E)): print "j is bad for " + str(t2)

res_scale = 10
t2 = [(np.radians(float(val) / res_scale))  for val in xrange(360 * res_scale + 1)]
Ax = []
Ay = []
Bx = []
By = []
Cx = []
Cy = []
Dx = []
Dy = []
Ex = []
Ey = []

  
plt.ion()
fig = plt.figure()
ax = fig.add_subplot(111)
lineA, = ax.plot(Ax, Ay, 'brown', label="A position")
lineB, = ax.plot(Bx, By, 'green', label="B position")
lineC, = ax.plot(Cx, Cy, 'blue', label="C position")
lineD, = ax.plot(Dx, Dy, 'orange', label="D position")
lineE, = ax.plot(Ex, Ey, 'red', label="E position")
handles, labels = ax.get_legend_handles_labels()
ax.legend(handles, labels)

for iter in xrange(len(t2)):
  poses = analyzePosition(t2[iter])
  checkPositions(t2[iter], poses[0], poses[1], poses[2], poses[3], poses[4])
  #print str(t2[iter]) + ":" + str(pos)
  Ax.append(poses[0][0])
  Ay.append(poses[0][1])
  Bx.append(poses[1][0])
  By.append(poses[1][1])
  Cx.append(poses[2][0])
  Cy.append(poses[2][1])
  Dx.append(poses[3][0])
  Dy.append(poses[3][1])
  Ex.append(poses[4][0])
  Ey.append(poses[4][1])

  if (iter % (res_scale*5)) == 0:
    lineA.set_xdata(Ax)
    lineA.set_ydata(Ay)
    lineB.set_xdata(Bx)
    lineB.set_ydata(By)
    lineC.set_xdata(Cx)
    lineC.set_ydata(Cy)
    lineD.set_xdata(Dx)
    lineD.set_ydata(Dy)
    lineE.set_xdata(Ex)
    lineE.set_ydata(Ey)
    ax.relim()
    ax.autoscale_view()
    fig.canvas.draw()
  
input("Press any key to close")