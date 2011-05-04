module Matrix where

type Vector = [Double]
type Matrix = [Vector]

accurracy :: Vector -> Vector -> Double
accurracy old new = maximum $ zipWith (-) old new

dominantDiagonal :: Matrix -> Bool
dominantDiagonal m = (length badVectors) == 0 
  where badVectors = filter (\(i,s) -> i <= s) $ map markList $ zipWith (\a b -> (a, b)) m [0..(length m)]
  
markList (v, i) = (mi, (sum m) - mi)
  where m = map abs v
        mi = m !! i
