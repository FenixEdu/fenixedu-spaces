import os
from bs4 import BeautifulSoup
import codecs

DIR="src/main/webapp";
RESOURCES="src/main/webapp/WEB-INF/resources/"
NEW_RESOURCES="/tmp/"
def parse_dir(directory, filter):
	file_paths = []
	for root, directories, files in os.walk(directory):
		for filename in files:
			filepath = os.path.join(root, filename)
			if (filter in filepath):
				file_paths.append(filepath)
	return file_paths



codes = {}

for jsp_file in parse_dir(DIR, ".jsp"):
	jsp = open(jsp_file).read()
	html = BeautifulSoup(jsp)
	for link in html.find_all('spring:message'):
		text = link.get("text") if link.get("text") != None else ""
		code = link.get('code')
		if not code.startswith("$"):
			if code in codes:
				there = codes[code]
				if isinstance(there, list):
					if not text in there:
						codes[code].append(text)
				else:
					if text != there:
						codes[code] = [there, text]
			else:
				codes[code] = text

# for code in sorted(codes):
# 	print "%s=%s" % (code, codes[code])

for rsr_file in parse_dir(RESOURCES, "SpacesResources"):
	print "process "  + rsr_file
	rsr = open(rsr_file).readlines()
	rsr_map = {}

	for r in rsr:
		if "=" in r:
			prop = r.strip().split("=")
			key = prop[0].strip()
			value = prop[1].strip()
			print ">%s<" % key
			rsr_map[key] = value

	for code in sorted(codes):
		if not code in rsr_map:
			rsr_map[code] = codes[code]
	
	fpath = rsr_file.split("/")
	nfpath = "%s/New%s" % ("/".join(fpath[:-1]), fpath[-1:][0])
	f = open(nfpath, "w")
	for code in sorted(rsr_map):
		prop = "%s=%s\n" % (code, rsr_map[code])
		f.write(prop)
	f.close()

# xmldoc = minidom.parse("labels.xml")
# messages = xmldoc.getElementsByTagName("spring:message")
# for message in messages:
# 	print "%s=%s" % (message.attributes['code'].value, message.attributes['text'].value)