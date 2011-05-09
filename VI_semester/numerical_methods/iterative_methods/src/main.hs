import Matrix
import IterativeMethod
import Zeidel
import Data.List(zip4)

epsilon = 0.0001
iterations = 1000

-- simple test suite
av = [[4.0, -1, -1], [6, 8, 0], [-5, 0, 12]]
bv = [-2.0, 45, 80]

-- specific task 
--av = [[1.362, 0.202, -0.599, 0.432], [0.202, 1.362, 0.202, -0.599], [-0.599, 0.202, 1.362, 0.202], [0.432, -0.599, 0.202, 1.362]]
--bv = [ 1.941, -2.3, -1.941, 0.23 ]

-- wikipedia test
--av = [[10, -1, 2, 0], [-1, 11, -1, 3], [2, -1, 10, -1], [0, 3, -1, 8]] 
--bv = [6, 25, -11, 15]

x = take (length av) [0.0,0..]

-- TODO: netiktis + convergence condition
-- TODO: implement conjugate gradient method

main :: IO()
main = do
  putStrLn $ " Iterations:  " ++ (show iterations) ++ " Epsilon: " ++ (show epsilon)
  putStrLn " ZEIDEL METHOD: "   
  showSolutionTrace' zd
  where zd = solveWithZeidelMethod av x bv iterations epsilon

showSolutionTrace' :: ZeidelMethod -> IO()
showSolutionTrace' z@(Zeidel a x b trace) = do
  putStrLn traceInfo
  putStrLn $ " In " ++ show (traceLen-1) ++ " iterations" 
  where traceLen  = length trace
        traceInfo = foldr (\(n, s, er, res) acc -> (acc ++ "\n" ++ (show n) ++ ": " ++ (show s) ++ " #Er: " ++ (show er) ++ "\tR: " ++ (show res))) "" info
        info      = zip4 [traceLen-1, traceLen-2 ..] trace (traceErrors' trace) (traceResiduals' z)
