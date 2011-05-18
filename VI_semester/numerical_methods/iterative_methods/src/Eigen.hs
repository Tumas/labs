module Eigen where

import Matrix

type EigenTrace = [(Double, Vector)]

av = [[-0.919, 0.722, 0, 0], [0.772, 1.151, -0.452, 0], [0, -0.452, 0.468, -1.166], [0, 0, -1.166, 1.9]]
--av2 = [[2.0, -1, 0, 0], [-1, 2.0, -1, 0], [0, -1, 2, -1], [0, 0, -1, 2]]

cPoly :: Matrix -> Int -> Double -> Double 
cPoly _ 0 _       = 1
cPoly m 1 lambda  = (nnElem m 1) - lambda
cPoly m i lambda  = (cn - lambda) * (cPoly m (i-1) lambda) - an * bn1 * (cPoly m (i-2) lambda)
  where cn  = nnElem m i  
        an  = aElemT m i 
        bn1 = bElemT m (i-1) 

solveE :: Matrix -> Vector -> Double -> Double -> Int -> Double -> EigenTrace
solveE a x a' b' it epsilon = findEigen a x lambda it epsilon [(lambda, x)]
  where lambda = (a' + b') / 2 

findEigen :: Matrix -> Vector -> Double -> Int -> Double -> EigenTrace -> EigenTrace
findEigen _ _ _      0  _       t = t
findEigen a x lambda it epsilon t 
  | l2Norm (subtract' x' x'') <= epsilon && (abs (l' - l'')) <= epsilon = t
  | otherwise                                         = findEigen a x l' (it-1) epsilon (result : t)
  where result@(l', x') = Eigen.iterate a x lambda
        (l'', x'')      = head t

iterate a x lambda = (l', x')
  where l' = dotProduct (multiply' a x') x'
        x' = map (\a -> a / sp) y
        sp = l2Norm y
        y  = multiply' rs x
        rs = inverse $ subtractMM a $ multiplyNM lambda (identity a)
