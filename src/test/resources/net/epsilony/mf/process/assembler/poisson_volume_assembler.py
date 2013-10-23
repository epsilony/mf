'''


@author: epsilonyuan@gmail.com
'''
import numpy as np
import common_tools


all_nodes_size = 13
random_seed = 47
rand = common_tools.gen_random_by_seed(random_seed)

def gen_test_datas():    
    result = [gen_test_data(spatial_dimension) for spatial_dimension in [1, 1, 2, 2, 2, 3, 3, 3]]
    return result

def gen_test_data(value_dimension):
    data = {}
    data['allNodesSize'] = all_nodes_size
    data['valueDimension'] = 1
    data['spatialDimension'] = value_dimension
    data['weight'] = rand.random()
    data['load'] = common_tools.gen_vector(1, rand)
    
    (assembly_indes, test_shape_function, trial_shape_function) = gen_test_trial_shape_function(value_dimension)
    data['testShapeFunction'] = test_shape_function
    data['trialShapeFunction'] = trial_shape_function
    data['assemblyIndes'] = assembly_indes
    
    (main_matrix_difference, main_vector_difference) = assemble_poisson_volume(data)
    
    data['mainMatrixDifference'] = main_matrix_difference
    data['mainVectorDifference'] = main_vector_difference
    
    return data

def gen_test_trial_shape_function(spatial_dimension):
    shape_func_nodes_num = rand.randint(1, all_nodes_size // 2)
    test_shape_function = common_tools.gen_matrix((1+spatial_dimension, shape_func_nodes_num), rand)
    trial_shape_function = common_tools.gen_matrix((1+spatial_dimension, shape_func_nodes_num), rand)
    assembly_indes = common_tools.gen_nodes_indes(shape_func_nodes_num, 0, all_nodes_size, rand)
    return (assembly_indes, test_shape_function, trial_shape_function)

def assemble_poisson_volume(data):
    spatical_dimension = data['spatialDimension']
    test_shape_function = common_tools.shape_func_to_whole_vector(data['testShapeFunction'], data['assemblyIndes'], all_nodes_size)
    trial_shape_function = common_tools.shape_func_to_whole_vector(data['trialShapeFunction'], data['assemblyIndes'], all_nodes_size)
    main_matrix_size = data['allNodesSize'];
    
    mat = np.zeros((main_matrix_size, main_matrix_size), dtype=np.double)
    vec = np.zeros((main_matrix_size,), dtype=np.double)
    
    weight = data['weight']
    for dim in range(spatical_dimension):
        test_vec = test_shape_function[dim + 1]
        trial_vec = trial_shape_function[dim + 1]
        mat += test_vec.reshape((-1, 1)).dot(trial_vec.reshape((1, -1))) * weight
     
    vec += data['load'][0] * weight * test_shape_function[0]
    return (mat, vec.reshape((vec.shape[0],1))) 

if __name__ == "__main__":
    test_data = gen_test_datas()
    print(common_tools.to_json_string(test_data))
