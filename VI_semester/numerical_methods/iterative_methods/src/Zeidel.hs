module Zeidel where

import Matrix

data ZeidelMethod = Zeidel
    { a        ::  Matrix 
    , solution ::  Vector
    , b        ::  Vector
    , trace    ::  Trace
    } deriving (Show, Eq)

solveWithZeidel :: ZeidelMethod -> Int -> Double -> ZeidelMethod
solveWithZeidel z@(Zeidel a _ _ _) maxIterations epsilon 
  | convergenceTest z == False  = error $ "Convergence test failed: " ++ show a
  | otherwise                   = solveZ z maxIterations epsilon
--  where z = Zeidel a x b [x]

convergenceTest :: ZeidelMethod -> Bool
convergenceTest (Zeidel a _ _ _) = (dominantDiagonally a) || ((isSymmetric a) && (isPositive a))

solveZ :: ZeidelMethod -> Int -> Double -> ZeidelMethod
solveZ   (Zeidel a x b t) 0 _   = Zeidel a x b (x:t)
solveZ z@(Zeidel a x b t) i epsilon
  | acc <= epsilon              = Zeidel a x b (x:t)
  | otherwise                   = solveZ nextZeidel (i-1) epsilon 
  where nextZeidel = iterateZ z                        
        acc        = accurracy x $ solution nextZeidel 
         
iterateZ :: ZeidelMethod -> ZeidelMethod
iterateZ (Zeidel a x b t)    = Zeidel a solutions b (solutions:t)
  where solutions            = fst $ foldr expressX ([], 0) x
        expressX el (acc, i) = (acc ++ [(getX (Zeidel a acc b t) x i)], i+1)

getX :: ZeidelMethod -> Vector -> Int -> Double
getX (Zeidel a x b t) v i = (bi - line) / aii
  where ai   = a  !! i
        bi   = b  !! i
        aii  = ai !! i
        xs   = x ++ (reverse $ take ((length b) - (length x)) (reverse v)) 
        line = (sum (zipWith (*) ai xs)) - (aii * (xs !! i))

traceErrors :: ZeidelMethod -> Vector
traceErrors z = collect' accurracy $ trace z

--  collect values by diff:
--      collect' (+) [1, 2, 3] -> [3, 5]
--      TO: iteration method
collect' :: (a -> a -> b) -> [a] -> [b]
collect' f src = snd $ foldr (\e1 (e2, acc) -> (e1, (f e1 e2) : acc)) (last src, []) $ init src
