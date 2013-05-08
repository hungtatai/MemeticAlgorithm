package ntu.honda.problem.tsp;

public enum TSPLocalSearch {
	None,
	First,
	Best;
	
	@Override
	public String toString() {
		if(this == TSPLocalSearch.None)
			return "TSPLocalSearch.None";
		else if(this == TSPLocalSearch.First)
			return "TSPLocalSearch.First";
		else if(this == TSPLocalSearch.Best)
			return "TSPLocalSearch.Best";
		else
			return "Error";
	}
}
