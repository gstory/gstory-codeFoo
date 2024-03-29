import sys

def minimizeExcess( plates, pop, strict ) :
	origPlates = plates[:]
	newPlates = plates[:]
	newerPlates = plates[:]
	
	excess = plates[3]
	newExcess = excess
	newerExcess = excess

	if( excess == 0 and plates[1] == 0 and plates[2] == 0 ) :
		return plates

	if( plates[2] > 0 and not strict ) :
		plates[2] = plates[2] - 1
		plates[1] = plates[1] + 1
				
		if( calcExcess(plates, pop) <= excess and calcExcess(plates, pop) >= 0 ) :
			plates[3] = calcExcess(plates, pop)
			origPlates = plates[:]
			newPlates = minimizeExcess(plates, pop, strict)
			newExcess = calcExcess(newPlates, pop)
		else :
			plates = origPlates[:]
		
	if( plates[1] > 0 ) :
		plates[1] = plates[1] - 1
		plates[0] = plates[0] + 1
	
		if( calcExcess(plates, pop) <= excess and calcExcess(plates, pop) >= 0  ) :
			plates[3] = calcExcess(plates, pop)
			newerPlates = minimizeExcess(plates, pop, strict)
			newerExcess = calcExcess(newerPlates, pop)
		else :
			plates = origPlates[:]
				
	if( calcExcess(origPlates, pop) <= calcExcess(plates, pop) or calcExcess(plates, pop) <= 0 ) :
		excess = calcExcess(origPlates, pop)
		plates = origPlates[:]
		
	if( newExcess <= excess and newExcess >= 0 and not strict) :
		excess = newExcess
		plates = newPlates[:]
		
	if( newerExcess <= excess and newExcess >= 0) :
		plates = newerPlates[:]
		
	return plates
	
	
def calcExcess( plates, pop ) :
	return ((plates[0] * 10) + (plates[1] * 26) + (plates[2] * 36)) - int(pop)
	


#Start
print("License Plates")
print(" - enter a population to determine results")
print(" - enter 0 to escape")	
print(" - enter \"strict plates\" to allow for only numbers OR letters (both allowed by default)") 
maxPlates = 36
strict = False

while( True ) :
	
	pop = "a"

	while( not isinstance(pop, int) ) :
		pop = raw_input("Enter population (or 0 to escape): ")
		
		if( pop == "strict plates" ):
			strict = True
			maxPlates = 26
		try:
			pop = int(pop)
		except ValueError:
			pop = pop
    		
	if( int(pop) is 0 ) :
		break
	
	numPlates = pop / maxPlates
	
	if( pop%maxPlates > 0 and numPlates > 0 ) :
		numPlates = numPlates + 1
		
	
	"""
		plates[0] = numbers
		plates[1] = letters
		plates[2] = numbers and letters
		plates[3] = excess	
	"""
	plates = [0]*(int(4))

	if( strict ) :
		plates[1] = numPlates
		excess = (numPlates * 26) - pop
	else :
		plates[2] = numPlates
		excess = (numPlates * 36) - pop
	

	plates[3] = excess


	if( numPlates == 0 and pop <= 10 ) :
		plates[0] = 1
	elif( numPlates == 0 and pop <= 26 ) :
		plates[1] = 1
	elif( numPlates == 0 and pop <= 36 ) :
		plates[2] = 1
	else :
		plates = minimizeExcess( plates, pop, strict )
	
	pattern = ""
	if( not plates[0] == 0 ):
		pattern += str(plates[0]) + " number"
		if( not plates[0] == 1 ):
			pattern += "s"
		if( plates[1] > 0 or plates[2] > 0 ) :
			pattern += ", "
	if( not plates[1] == 0 ):
		pattern += str(plates[1]) + " letter"
		if( not plates[1] == 1 ):
			pattern += "s"
		if( plates[2] > 0 ) :
			pattern += ", "
	if( not plates[2] == 0 ):
		pattern += str(plates[2]) + " number"
		if( not plates[2] == 1 ):
			pattern += "s"
		pattern += " or letter"
		if( not plates[2] == 1 ):
			pattern += "s"
	
	print "Population: " + str(pop)
	print "Pattern: " + pattern
	print "Total Plates: " + str((plates[0] * 10) + (plates[1] * 26) + (plates[2] * 36))
	print "Excess Plates: " + str(calcExcess(plates, pop))

