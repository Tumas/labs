import Matrix
import Zeidel

epsilon = 0.0001
iterations = 1000

-- simple test suite
av = [[4.0, -1, -1], [6, 8, 0], [-5, 0, 12]]
bv = [-2.0, 45, 80]

-- specific task 
--av = [[1.362, 0.202, -0.599, 0.432], [0.202, 1.362, 0.202, -0.599], [-0.599, 0.202, 1.362, 0.202], [0.432, -0.599, 0.202, 1.362]]
--bv = [ 1.941, -2.3, -1.941, 0.23 ]

x = take (length av) [0.0,0..]

-- TODO: zeidel convergence test
-- TODO: trace printing 
-- TODO: implement conjugate gradient method
-- TODO: hspec testing, fix cabal 

main :: IO()
main = do
  putStrLn " ZEIDEL METHOD: " 
  showSolutionTrace $ solveWithZeidelMethod av x bv iterations epsilon
  --showSolutionTrace solveWithConjugentGradient a x b iterations epsilon

showSolutionTrace :: [Vector] -> IO()
showSolutionTrace trace = do
  putStrLn traceInfo
  putStrLn $ " In " ++ show size ++ " iterations" 
  where size   = length trace
        traceInfo = foldr (\s acc -> (acc ++ "\n" ++ (show s))) "" $ zip [traceLen, traceLen-1 ..] trace
        traceLen = length trace
