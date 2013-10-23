'''


@author: epsilonyuan@gmail.com
'''


import numpy as np
import common_tools


all_nodes_size = 13
random_seed = 47
rand = common_tools.gen_random_by_seed(random_seed)

def gen_test_datas():    
    result = [gen_test_data(value_dimension) for value_dimension in [1, 1, 2, 2, 2, 3, 3, 3]]
    return result

def gen_test_data(value_dimension):
    data = {}
    data['allNodesSize'] = all_nodes_size
    data['valueDimension'] = value_dimension;
    data['weight'] = rand.random()
    data['load'] = common_tools.gen_vector(value_dimension, rand)
    data['constitutiveLaw'] = common_tools.gen_constitutive(value_dimension, rand)
    
    (assembly_indes, test_shape_function, trial_shape_function) = gen_test_trial_shape_function(value_dimension)
    data['testShapeFunction'] = test_shape_function
    data['trialShapeFunction'] = trial_shape_function
    data['assemblyIndes'] = assembly_indes
    
    (main_matrix_difference, main_vector_difference) = assemble_mechanical_volume(data)
    
    data['mainMatrixDifference'] = main_matrix_difference
    data['mainVectorDifference'] = main_vector_difference
    
    return data

def gen_test_trial_shape_function(spatial_dimension):
    shape_func_nodes_num = rand.randint(1, all_nodes_size // 2)
    test_shape_function = common_tools.gen_matrix((1 + spatial_dimension, shape_func_nodes_num), rand)
    trial_shape_function = common_tools.gen_matrix((1 + spatial_dimension, shape_func_nodes_num), rand)
    assembly_indes = common_tools.gen_nodes_indes(shape_func_nodes_num, 0, all_nodes_size, rand)
    return (assembly_indes, test_shape_function, trial_shape_function)

def assemble_volume_mat_1d(test_shape_func, c_law, trial_shape_func):
    left = test_shape_func[1];
    right = trial_shape_func[1];
    return c_law[0][0] * left.reshape((left.shape[0], 1)).dot(right.reshape((1, right.shape[0])))

def assemble_volume_mat_2d(test_shape_func_whole, c_law, trial_shape_func_whole):
    left = np.zeros((3, 2 * test_shape_func_whole.shape[1]), dtype=np.double)
    right = np.zeros((3, 2 * trial_shape_func_whole.shape[1]), dtype=np.double)
    for n, shape_func in zip((left, right), (test_shape_func_whole, trial_shape_func_whole)):
        n[0, 0::2] = shape_func[1]
        n[1, 1::2] = shape_func[2]
        n[2, 0::2] = shape_func[2]
        n[2, 1::2] = shape_func[1]
    
    return left.transpose().dot(c_law).dot(right)

def assemble_volume_mat_3d(test_shape_func_whole, c_law, trial_shape_func_whole):
    left = np.zeros((6, 3 * test_shape_func_whole[0].shape[0]), dtype=np.double)
    right = np.zeros((6, 3 * trial_shape_func_whole[0].shape[0]), dtype=np.double)
    
    for n, shape_func in zip((left, right), (test_shape_func_whole, trial_shape_func_whole)):
        n[0, 0::3] = shape_func[1]
        n[1, 1::3] = shape_func[2]
        n[2, 2::3] = shape_func[3]
        n[3, 0::3] = shape_func[2]
        n[3, 1::3] = shape_func[1]
        n[4, 1::3] = shape_func[3]
        n[4, 2::3] = shape_func[2]
        n[5, 0::3] = shape_func[3]
        n[5, 2::3] = shape_func[1]

    return left.transpose().dot(c_law).dot(right)

_assemble_volume_mat = (assemble_volume_mat_1d, assemble_volume_mat_2d, assemble_volume_mat_3d)

def assemble_volume_mat(value_dimension, weight, test_shape_func_whole, c_law, trial_shape_func_whole):
    f = _assemble_volume_mat[value_dimension - 1]
    return weight * f(test_shape_func_whole, c_law, trial_shape_func_whole)

def assemble_mechanical_volume(data):
    test_shape_function = common_tools.shape_func_to_whole_vector(data['testShapeFunction'], data['assemblyIndes'], all_nodes_size)
    trial_shape_function = common_tools.shape_func_to_whole_vector(data['trialShapeFunction'], data['assemblyIndes'], all_nodes_size)   
    value_dimension = data['valueDimension']
    load = data['load']
    weight = data['weight']
    c_law = data['constitutiveLaw']
    
    mat = assemble_volume_mat(value_dimension,weight, test_shape_function, c_law, trial_shape_function)
    import neumann_assembler
    vec = neumann_assembler.assemble_general_force(weight, load, test_shape_function)
    return (mat, vec.reshape((vec.shape[0],1))) 

if __name__ == "__main__":
    test_data = gen_test_datas()
    print(common_tools.to_json_string(test_data))
