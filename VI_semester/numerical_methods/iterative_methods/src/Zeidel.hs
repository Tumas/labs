module Zeidel  where

import Matrix

type Trace  = [Vector]

data ZeidelMethod = Zeidel
    { a        ::  Matrix 
    , solution ::  Vector
    , b        ::  Vector
    , trace    ::  Trace
    } deriving (Show)

solveWithZeidelMethod :: Matrix -> Vector -> Vector -> Int -> Double -> Trace
solveWithZeidelMethod a x b maxIterations epsilon 
  | convergenceTest z == False  = error $ "Convergence test failed: " ++ show a
  | otherwise                   = solve z maxIterations epsilon
  where z = Zeidel a x b []

convergenceTest :: ZeidelMethod -> Bool
convergenceTest (Zeidel a _ _ _) = dominantDiagonal a

solve :: ZeidelMethod -> Int -> Double -> Trace
solve   (Zeidel _ x _ t) 0 _    = x : t
solve z@(Zeidel a x b t) i epsilon
  | acc <= epsilon              = x : t
  | otherwise                   = solve nextZeidel (i-1) epsilon 
  where nextZeidel = iterateZ z                        
        acc        = accurracy x $ solution nextZeidel 
         
iterateZ :: ZeidelMethod -> ZeidelMethod
iterateZ (Zeidel a x b t)      = Zeidel a solutions b (solutions:t)
  where solutions            = fst $ foldr expressX ([], 0) x
        expressX el (acc, i) = (acc ++ [(getX (Zeidel a acc b t) x i)], i+1)

getX :: ZeidelMethod -> Vector -> Int -> Double
getX (Zeidel a x b t) v i = (bi - line) / aii
  where ai   = a  !! i
        bi   = b  !! i
        aii  = ai !! i
        xs   = x ++ take ((length b) - (length x)) v 
        line = (sum (zipWith (*) ai xs)) - (aii * (xs !! i))
