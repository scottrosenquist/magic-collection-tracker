fs = require('fs');
var allsets = JSON.parse(fs.readFileSync('all_sets.json').toString());
for(var setname in allsets){
    for(var setdata in allsets[setname]){
    	if(setdata == 'cards'){
    		allsets[setname][setdata] = allsets[setname][setdata].length;
    	}
    }
}
fs.writeFile('all_sets_trimmed.json', JSON.stringify(allsets));