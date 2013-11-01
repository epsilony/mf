'''
Created on 2013年10月31日

open py4j server before running this script!

@author: epsilon
'''

from py4j.java_gateway import JavaGateway

class Py4JConnector(object):
    def __init__(self):
        self.gateway = JavaGateway()
        self.root_package = self.gateway.jvm.net.epsilony.mf
        self.process = self.root_package.process
        self.project = self.root_package.project
        self.sample = self.project.sample
        self.Example = self.sample.TwoDPoissonBigDecimalBackExample
        self.SampleCase = self.sample.TwoDPoissonSampleFactory.SampleCase
        self.MFPreprocessorKey = self.process.MFPreprocessorKey

def fetch_integrator(processor, connector):
    settings = processor.getSettings()
    return settings.get(connector.MFPreprocessorKey.INTEGRATOR)

def fetch_records_observer(integrator):
    observer_name = "net.epsilony.mf.process.integrate.observer.CoreRecorderObserver"
    observers = integrator.getObservers()
    for ob in observers:
        if ob.getClass().getName() == observer_name:
            return ob

def fetch_records(integrator):
    observer = fetch_records_observer(integrator)
    return observer.getRecords()

def sorted_records(records):
    return sorted(records, key=lambda ob:list(ob.getCoord()))

def array_attr_to_list(obj, name):
    java_value = getattr(obj, name)()
    if java_value is None:
        return None
    return list(java_value)

def compare_record_array_attr(rec_a, rec_b, name):
    diff = False
    a_value = array_attr_to_list(rec_a, name)
    b_value = array_attr_to_list(rec_b, name)
    if a_value != b_value:
        print("attr " + name + " different")
        print(a_value)
        print(b_value)
        diff = True
    return diff

_record_array_attrs = ["getCoord", "getOutNormal", "getLoad", "getLoadValidity","getLagrangleShapeFunction"]

def compare_record(rec_a, rec_b):
    diff = False
    for name in _record_array_attrs:
        diff2 = compare_record_array_attr(rec_a, rec_b, name)
        diff = diff or diff2
    diff2 = compare_weight(rec_a,rec_b)
    diff = diff or diff2
    diff2 = compare_lagrangle_indes(rec_a,rec_b)
    diff = diff or diff2
    diff2 = compare_record_mix_result(rec_a, rec_b)
    diff = diff or diff2
    return diff 

def compare_weight(rec_a,rec_b):
    a_w=rec_a.getWeight()
    b_w=rec_b.getWeight()
    if a_w != b_w:
        print("weight diff")
        print(a_w)
        print(b_w)
        return True
    return False

def compare_lagrangle_indes(rec_a,rec_b):
    a_indes=None if rec_a.getLagrangleIndes() is None else list(rec_a.getLagrangleIndes().toArray())
    b_indes=None if rec_b.getLagrangleIndes() is None else list(rec_b.getLagrangleIndes().toArray())
    if a_indes!=b_indes:
        print("lagrangle indes diff")
        print(a_indes)
        print(b_indes)
        return True
    return False

def compare_record_mix_result(rec_a, rec_b):
    diff = False
    a_nds_indes = get_record_nodes_indes(rec_a)
    b_nds_indes = get_record_nodes_indes(rec_b)
    if a_nds_indes != b_nds_indes:
        print("indes diff")
        print(a_nds_indes)
        print(b_nds_indes)
        diff = True
    a_sf = get_record_shape_funcs(rec_a)
    b_sf = get_record_shape_funcs(rec_b)
    if a_sf != b_sf:
        print("shape func diff")
        print(a_sf)
        print(b_sf)
        diff = True
    if len(a_nds_indes) != len(a_sf[0]):
        print("shape indes length mismatch")
        print("indes " + str(len(a_nds_indes)))
        print("sp_func " + str(len(a_sf[0])))
    return diff
    
def get_record_nodes_indes(rec):
    mixResult = rec.getMixResult()
    nds_indes = mixResult.getNodesAssemblyIndes()
    nds_indes = list(nds_indes.toArray())
    return nds_indes

def get_record_shape_funcs(rec):
    mixResult = rec.getMixResult()
    shape_func = mixResult.getShapeFunctionValues()
    shape_func = list(shape_func)
    for i in range(len(shape_func)):
        s = shape_func[i]
        shape_func[i] = list(s)
    return shape_func

def compare_records(recs_a, recs_b):
    diff_index = 0
    for rec_a, rec_b in zip(recs_a, recs_b):
        diff = compare_record(rec_a, rec_b)
        if diff :
             print("diff at: " + str(diff_index))
        diff_index += 1
    
class ExampleIncarnate(object):
    def __init__(self, connector):
        self.sample_case = connector.SampleCase.LINEAR
        self.single = connector.Example.singleThread(self.sample_case)
        self.multi = connector.Example.multiThread(self.sample_case)
        self.single_integrator = fetch_integrator(self.single.getProcessor(), connector)
        self.multi_integrator = fetch_integrator(self.multi.getProcessor(), connector)
        self.single_records = fetch_records(self.single_integrator)
        self.multi_records = fetch_records(self.multi_integrator)
        self.single_records_sorted = sorted_records(self.single_records)
        self.multi_records_sorted = sorted_records(self.multi_records)
        
        
    
        
if __name__ == '__main__':
    connector = Py4JConnector()
    example = ExampleIncarnate(connector)
    compare_records(example.single_records_sorted, example.multi_records_sorted)
