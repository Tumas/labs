module IterativeMethod where

import Matrix
import qualified Zeidel as Z
import qualified ConjugentGradient as C

class IterativeMethod a where
  solve    :: a -> Int -> Double -> a 
  solution :: a -> Vector
  trace    :: a -> Trace
  traceErrors :: a -> Vector 

  a :: a -> Matrix
  b :: a -> Vector

instance IterativeMethod Z.ZeidelMethod where
  solve    = Z.solveWithZeidel
  solution = Z.solution
  trace    = Z.trace
  traceErrors = Z.traceErrors
  a = Z.a
  b = Z.b

instance IterativeMethod C.CGradient where
  solve    = C.solveWithCG
  solution = C.solution
  trace    = C.trace
  traceErrors = C.traceErrors
  a = C.a
  b = C.b

traceResiduals' :: IterativeMethod i => i -> Vector
traceResiduals' im = foldr (\v acc -> (residual v) : acc) [] $ trace im
    where residual xx = maximum $ subtract' (multiply' (a im) xx) (b im) 

