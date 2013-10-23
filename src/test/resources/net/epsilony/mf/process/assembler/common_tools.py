'''


@author: epsilonyuan@gmail.com
'''
import numpy as np
import random
from json import JSONEncoder
import json

class NumpyEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return super().default(obj)
    
def write_to_json_file(file_name, test_data):
    with open(file_name, 'w') as fop:
        json.dump(test_data, fop, indent=4, cls=NumpyEncoder)

def to_json_string(test_data):
    from io import StringIO
    sio = StringIO()
    json.dump(test_data, sio, indent=4, cls=NumpyEncoder)
    return sio.getvalue()

def gen_random_by_seed(seed):
    return random.Random(seed)

def gen_constitutive(value_dimension, rand):
    sizes = (1, 3, 6)
    size = sizes[value_dimension - 1]
    result = np.ndarray((size, size), dtype=np.double)
    for i in range(size):
        for j in range(i, size):
            result[i][j] = rand.random()
            result[j][i] = result[i][j]
    return result

def gen_matrix(shape, rand):
    result = np.zeros(shape, dtype=np.double)
    for row in range(shape[0]):
        for col in range(shape[1]):
            result[row][col] = rand.random()
    return result

def gen_vector(length, rand):
    return gen_matrix((length, 1), rand).reshape((length,))
            
def gen_nodes_indes(size, index_from, index_to, rand):
    all_indes = [i for i in range(index_from, index_to)]
    random.shuffle(all_indes, rand.random)
    return all_indes[:size]

def shape_func_to_whole_vector(shape_func_by_diff, indes, all_nodes_size):
    result = np.zeros((shape_func_by_diff.shape[0], all_nodes_size), dtype=np.double)
    for i in range(len(indes)):
        result[:, indes[i]] = shape_func_by_diff[:, i]
    return result


