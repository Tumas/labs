module Matrix where

import Data.List(transpose)

type Vector = [Double]
type Matrix = [Vector]
type Trace  = [Vector] 

accurracy :: Vector -> Vector -> Double
accurracy old new = maximum $ map abs $ zipWith (-) old new

-- Matrix operations
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
