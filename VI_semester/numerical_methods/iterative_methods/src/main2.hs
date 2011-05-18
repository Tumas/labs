import Matrix
import Eigen
import System.Environment
import qualified Graphics.Gnuplot.Simple as G
import Data.String.Utils(replace)

epsilon    = 0.0001
iterations = 1000

--av = [[2.94, 2, 0, 0], [2, 2.94, 2, 0], [0, 2, 2.94, 2], [0, 0, 2, 2.94]]
av2 = [[2.0, -1, 0, 0], [-1, 2.0, -1, 0], [0, -1, 2, -1], [0, 0, -1, 2]]
av = [[-0.919, 0.722, 0, 0], [0.772, 1.151, -0.452, 0], [0, -0.452, 0.468, -1.166], [0, 0, -1.166, 1.9]]

fyx :: Double -> Double
fyx a = 0 

reprM :: Matrix -> String
reprM m = replace "]" "}" (replace "[" "{" (show m))

main :: IO()
main = do
  plotPolys [Main.av] 100 (-5.0) 5.0 
  putStrLn $ reprM Main.av
  a <- getLine
  b <- getLine
  putStrLn $ formatEigenTrace $ solveE Main.av [1, 1, 1, 1] (read a) (read b) iterations epsilon
  return ()

plotPolys l sc from to = G.plotFuncs options (G.linearScale sc (from, to)) $ fyx : funks
  where funks = map (\m -> cPoly m (length m)) l 
        options = [G.YRange (-50.0, 50.0)]

formatEigenTrace :: EigenTrace -> String
formatEigenTrace t = foldr (\(lambda, x) acc -> (acc ++ "\n" ++ (show lambda) ++ ": " ++ (show x))) "" t
