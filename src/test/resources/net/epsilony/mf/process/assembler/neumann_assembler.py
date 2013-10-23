'''


@author: epsilonyuan@gmail.com
'''


import numpy as np
import common_tools


all_nodes_size = 13
random_seed = 47
rand = common_tools.gen_random_by_seed(random_seed)

def gen_test_datas():    
    result = [gen_test_data(value_dimension) for value_dimension in [1, 1, 2, 2, 3, 3]]
    return result

def gen_test_data(value_dimension):
    data = {}
    data['allNodesSize'] = all_nodes_size
    data['valueDimension'] = value_dimension
    data['weight'] = rand.random()
    data['load'] = common_tools.gen_vector(value_dimension, rand)
    
    shape_func_nodes_num = rand.randint(1, all_nodes_size // 2)
    test_shape_function = common_tools.gen_matrix((1, shape_func_nodes_num), rand)
    nodes_assemble_indes = common_tools.gen_nodes_indes(shape_func_nodes_num, 0, all_nodes_size, rand)
    
    data['testShapeFunction'] = test_shape_function
    data['assemblyIndes'] = nodes_assemble_indes
    
    whole_test_shape_function_vector = common_tools.shape_func_to_whole_vector(test_shape_function, nodes_assemble_indes, all_nodes_size)
    data['mainVectorDifference'] = assemble_general_force(data['weight'], data['load'], whole_test_shape_function_vector)
    
    return data

def assemble_general_force(weight, load, whole_test_shape_func_by_diff):
    shape_func = whole_test_shape_func_by_diff[0];
    value_dim = load.shape[0]
    main_matrix_size = shape_func.shape[0] * value_dim
    result = np.zeros((main_matrix_size, 1), np.double)
    for i in range(value_dim):
        result[i::value_dim, 0] = weight * load[i] * shape_func
    return result

if __name__ == "__main__":
    test_data = gen_test_datas()
    print(common_tools.to_json_string(test_data))
