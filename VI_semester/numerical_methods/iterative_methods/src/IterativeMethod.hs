module IterativeMethod where

import Matrix
import qualified Zeidel as Z

class IterativeMethod a where
  solve   :: a -> Int -> Double -> a 
  iterate :: a -> a

  solution :: a -> Vector
  trace    :: a -> Trace
  a :: a -> Matrix
  b :: a -> Vector

instance IterativeMethod Z.ZeidelMethod where
  solve    = Z.solveZ
  iterate  = Z.iterateZ
  solution = Z.solution
  trace    = Z.trace
  a = Z.a
  b = Z.b

traceResiduals' :: IterativeMethod i => i -> Vector
traceResiduals' im = foldr (\v acc -> (residual v) : acc) [] $ trace im
  where residual xx = (multiply' (a im) xx) - (sum (b im))

traceErrors' :: Trace -> Vector
traceErrors' t = collect' accurracy t

--  collect values by diff:
--      collect' (+) [1, 2, 3] -> [3, 5]
--      TO: iteration method
collect' :: (a -> a -> b) -> [a] -> [b]
collect' f src = snd $ foldr (\e1 (e2, acc) -> (e1, (f e1 e2) : acc)) (last src, []) $ init src
