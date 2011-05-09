module Matrix 
(
  Vector,
  Matrix,
  Trace,
  accurracy,
  dominantDiagonally,
  isPositive,
  isSymmetric,
  multiply'
)
where

type Vector = [Double]
type Matrix = [Vector]
type Trace  = [Vector] 

accurracy :: Vector -> Vector -> Double
accurracy old new = maximum $ map abs $ zipWith (-) old new

dominantDiagonally :: Matrix -> Bool
dominantDiagonally m = (length badVectors) == 0 
  where badVectors = filter (\(i,s) -> i <= s) $ map markList $ zipWith (\a b -> (a, b)) m [0..(length m)]

diagonallyDominantByRow :: Matrix -> Bool
diagonallyDominantByRow m = True

isPositive :: Matrix-> Bool
isPositive m = True

isSymmetric :: Matrix -> Bool
isSymmetric m = True

markList (v, i) = (mi, (sum m) - mi)
  where m = map abs v
        mi = m !! i

multiply' :: Matrix -> Vector -> Double
multiply' m v = foldr (\v1 val -> (sum $ zipWith(*) v1 v) + val) 0.0 m 
