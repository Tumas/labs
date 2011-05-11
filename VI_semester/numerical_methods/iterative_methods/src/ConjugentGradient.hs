module ConjugentGradient where 

import Matrix

data CGradient = CG
    { a        ::  Matrix 
    , solution ::  Vector
    , b        ::  Vector
    , trace    ::  Trace
    } deriving (Show, Eq)

convergenceTest :: CGradient -> Bool
convergenceTest (CG a _ _ _) = (isSymmetric a) && (isPositive a)

solveWithCG :: CGradient -> Int -> Double -> CGradient
solveWithCG g@(CG a _ _ _) it epsilon
  | convergenceTest g == False = error $ "Convergence test failed: " ++ show a
  | otherwise                  = solveCG g z z it (epsilon*epsilon)
  where z = getZ g

solveCG :: CGradient -> Vector -> Vector -> Int -> Double -> CGradient
solveCG (CG a x b t) _ _ 0 _ = CG a x b (x:t)
solveCG g@(CG a x b t) z p i epS 
  | err < epS  = CG a x b (x:t)
  | otherwise  = solveCG nextCG z1 p1 (i-1) epS
  where err               = dotProduct z z
        (nextCG, z1, p1)  = iterateCG g z p 

iterateCG :: CGradient -> Vector -> Vector -> (CGradient, Vector, Vector)
iterateCG (CG a x b t) z p = ((CG a x1 b (x1:t)), z1, p1)
  where r     = multiply' a p  
        gamma = (dotProduct z p) / (dotProduct r p)
        x1    = subtract' x (map (*gamma) p)
        z1    = subtract' z (map (*gamma) r)
        beta  = (dotProduct z1 z1) / (dotProduct z z)
        p1    = add' z1 (map (*beta) p)

getZ :: CGradient -> Vector
getZ (CG a x b _) = subtract' (multiply' a x) b

traceErrors :: CGradient -> Vector
traceErrors (CG a _ b t) = foldr (\x acc -> (errorCG (CG a x b []):acc)) [] t

errorCG :: CGradient -> Double
errorCG g = dotProduct z z 
  where z = getZ g
