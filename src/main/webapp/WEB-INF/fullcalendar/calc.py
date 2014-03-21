x = []
for i in range(2,22):
	x.append("MULTIPLY(%s,%s)" % ("B" + str(i) , "$J" + str(i)))

print "=SUM(%s)" % (",".join(x))