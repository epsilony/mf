'''

@author: Man YUAN <epsilonyuan@gmail.com>

'''

from mechanical_penalty_assembler import gen_sample_shape_func, gen_sample_indes, to_whole_vector

import numpy as np


nodes_num = 15
lag_nodes_num = 3
shape_func_size=4
random_seed = 1147

def gen_test_data_elem(dim, rand):
    data_elem = {}
    data_elem['nodes_num']=nodes_num;
    data_elem['lagrangle_nodes_num']=lag_nodes_num;
    data_elem['dimension'] = dim
    data_elem['test_shape_function'] = gen_sample_shape_func(dim, shape_func_size, rand)
    data_elem['trial_shape_function'] = gen_sample_shape_func(dim, shape_func_size, rand)
    data_elem['nodes_assembly_indes'] = gen_sample_indes(shape_func_size, nodes_num, rand)
    data_elem['weight'] = rand.random()
    data_elem['load'] = np.array([rand.random() for _i in range(dim)], dtype=np.double)
    
    whole_indes_size = (nodes_num + lag_nodes_num)
    whole_vec_size=whole_indes_size*dim
    
    test_shape_func = to_whole_vector(data_elem['test_shape_function'], data_elem['nodes_assembly_indes'], whole_indes_size)
    trial_shape_func = to_whole_vector(data_elem['trial_shape_function'], data_elem['nodes_assembly_indes'], whole_indes_size)
    
    mat = np.zeros((whole_vec_size, whole_vec_size), dtype=np.double)
    vec = np.zeros((whole_vec_size,), dtype=np.double)
    
    test_vec = np.zeros((whole_vec_size,), dtype=np.double)
    trial_vec = np.zeros((whole_vec_size,), dtype=np.double)
    
    for i in range(dim):
        test_vec[i::dim] = test_shape_func[i + 1]
        trial_vec[i::dim] = trial_shape_func[i + 1]
    
    weight = data_elem['weight']
    
    mat += test_vec.reshape((-1, 1)).dot(trial_vec.reshape((1, -1))) * weight
    
    for i in range(dim):
        vec[i::dim] += weight * data_elem['load'][i] * weight * test_vec[i::dim]
    
    data_elem['main_matrix']=mat
    data_elem['main_vector']=vec
    
    return data_elem

def gen_test_datas():
    result = []
    for dim in range(1, 4):
        import random as rand
        rand.seed(random_seed)
        data_elem=gen_test_data_elem(dim,rand)
        result.append(data_elem)
    return result

from json import JSONEncoder

class NumpyEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return super().default(obj)
    
def gen_test_data_json_file():
    file_name = "possion_assemblier.json"
    with open(file_name, 'w') as fop:
        import json
        json.dump(gen_test_datas(), fop, indent=4, cls=NumpyEncoder)

def gen_test_data_json_string():
    from io import StringIO
    sio=StringIO()
    import json
    json.dump(gen_test_datas(),sio,indent=4,cls=NumpyEncoder)
    return sio.getvalue()

if __name__ == "__main__":
    print(gen_test_data_json_string())

