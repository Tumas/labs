module Matrix where

import Data.List(transpose)
import Data.List.Split(splitEvery)
import qualified Matrix.LU (inverse)  
import qualified Array as A

type Vector = [Double]
type Matrix = [Vector]
type Trace  = [Vector] 

accurracy :: Vector -> Vector -> Double
accurracy old new = maximum $ map abs $ zipWith (-) old new

-- Matrix operations
multiplyNM :: Double -> Matrix -> Matrix
multiplyNM n m = map (multiplyNV n) m

subtractMM :: Matrix -> Matrix -> Matrix
subtractMM = zipWith subtract'

dominantDiagonally :: Matrix -> Bool
dominantDiagonally m = (length badVectors) == 0 
  where badVectors = filter (\(i,s) -> i <= s) $ map markList $ zipWith (\a b -> (a, b)) m [0..(length m)]

isPositive :: Matrix-> Bool
isPositive m = True

isSymmetric :: Matrix -> Bool
isSymmetric m = (transpose m) == m 

markList (v, i) = (mi, (sum m) - mi)
  where m = map abs v
        mi = m !! i

identity m = snd $ foldr (\el (i, acc) -> (i-1, (getIdentRow width (i-1)) : acc)) ((length m), []) m
  where width = length m

getIdentRow l i = (take i lst) ++ [1] ++ (take (l-i-1) lst)
  where lst = [0.0,0.0..]

nnElem :: Matrix -> Int -> Double
nnElem m i = (m !! (i-1)) !! (i-1)

inverse :: Matrix -> Matrix 
inverse m = splitEvery width $ A.elems $ Matrix.LU.inverse $ A.listArray ((1, 1), ((length m), width)) (concat m)
  where width = length $ head m

-- operations for tridiagonal matrix
aElemT :: Matrix -> Int -> Double
aElemT m i 
  | i <= 1    = error "No such element exists" 
  | otherwise = (m !! (i-1))  !! (i-2)

bElemT :: Matrix -> Int -> Double
bElemT m i 
  | i < 1     = error "No such element exists" 
  | otherwise = (m !! (i-1))  !! i

insertAt :: (Num a) => a -> Int -> [a] -> [a]
insertAt e i l = (take i l) ++ [e] ++ (drop i l)

-- Matrix-Vector operations
multiply' :: Matrix -> Vector -> Vector
multiply' m v = foldr (\row acc -> (sum $ zipWith (*) row v) : acc) [] m 

-- Vector - Vector operations
subtract' :: Vector -> Vector -> Vector
subtract' = zipWith (-) 

add' :: Vector -> Vector -> Vector
add' = zipWith (+)

dotProduct :: Vector -> Vector -> Double
dotProduct a b = sum $ zipWith (*) a b

multiplyNV :: Double -> Vector -> Vector
multiplyNV n v = map (*n) v

l2Norm :: Vector -> Double
l2Norm v = sqrt $ sum $ map (\x -> x*x) v
