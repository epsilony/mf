'''


@author: epsilonyuan@gmail.com
'''

import numpy as np
import common_tools


all_nodes_size = 13
all_lagrangle_nodes_size = 7
random_seed = 47
rand = common_tools.gen_random_by_seed(random_seed)

value_dimensions=[1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3]
load_validities = [None for _i in range(len(value_dimensions))]
load_validities[0]=[False]
load_validities[1]=[True]
load_validities[2]=[False,True]
load_validities[6]=[True,False,True]

def gen_test_datas():    
    result = [gen_test_data(value_dimension,load_validity) for value_dimension,load_validity in zip(value_dimensions,load_validities)]
    return result

def gen_test_data(value_dimension,load_validity):
    data = {}
    data['allNodesSize'] = all_nodes_size
    data['allLagrangleNodesSize']=all_lagrangle_nodes_size
    data['valueDimension'] = value_dimension
    data['weight'] = rand.random()
    data['load'] = common_tools.gen_vector(value_dimension, rand)
    data['loadValidity'] = load_validity if None is not load_validity else common_tools.gen_vector(value_dimension, rand) >= 0.5
    
    (assembly_indes, test_shape_function, trial_shape_function) = gen_test_trial_shape_function()
    data['testShapeFunction'] = test_shape_function
    data['trialShapeFunction'] = trial_shape_function
    data['assemblyIndes'] = assembly_indes
    
    (lagrangle_assemble_indes, lagrangle_shape_function) = gen_lagrangle_shape_function()
    data['lagrangleShapeFunction'] = lagrangle_shape_function
    data['lagrangleAssemblyIndes'] = lagrangle_assemble_indes
    
    (main_matrix_difference, main_vector_difference) = assemble_lagrangle_dirichlet(data)
    
    data['mainMatrixDifference'] = main_matrix_difference
    data['mainVectorDifference'] = main_vector_difference
    
    return data

def gen_test_trial_shape_function():
    shape_func_nodes_num = rand.randint(1, all_nodes_size // 2)
    test_shape_function = common_tools.gen_matrix((1, shape_func_nodes_num), rand)
    trial_shape_function = common_tools.gen_matrix((1, shape_func_nodes_num), rand)
    assembly_indes = common_tools.gen_nodes_indes(shape_func_nodes_num, 0, all_nodes_size, rand)
    return (assembly_indes, test_shape_function, trial_shape_function)

def gen_lagrangle_shape_function():
    lagrangle_shape_func_nodes_num = rand.randint(1, all_lagrangle_nodes_size)
    lagrangle_shape_function = common_tools.gen_matrix((1, lagrangle_shape_func_nodes_num), rand)
    lagrangle_assemble_indes = common_tools.gen_nodes_indes(lagrangle_shape_func_nodes_num, all_nodes_size, all_nodes_size + all_lagrangle_nodes_size, rand)
    return (lagrangle_assemble_indes, lagrangle_shape_function)

def to_whole_shape_function(shape_function, nodes_assemble_indes):
    return common_tools.shape_func_to_whole_vector(shape_function, nodes_assemble_indes, all_nodes_size + all_lagrangle_nodes_size)

def assemble_lagrangle_dirichlet(data):
    test_shape_function = common_tools.shape_func_to_whole_vector(data['testShapeFunction'], data['assemblyIndes'], all_nodes_size + all_lagrangle_nodes_size)
    trial_shape_function = common_tools.shape_func_to_whole_vector(data['trialShapeFunction'], data['assemblyIndes'], all_nodes_size + all_lagrangle_nodes_size)
    lagrangle_shape_function = common_tools.shape_func_to_whole_vector(data['lagrangleShapeFunction'], data['lagrangleAssemblyIndes'], all_nodes_size + all_lagrangle_nodes_size)
    
    value_dimension = data['valueDimension']
    load_validity = data['loadValidity']
    left = np.zeros((value_dimension, lagrangle_shape_function.shape[1] * value_dimension), np.double)
    right = np.zeros((value_dimension, lagrangle_shape_function.shape[1] * value_dimension), np.double)
    lag = np.zeros((value_dimension, lagrangle_shape_function.shape[1] * value_dimension), np.double)
    for n, v in zip((left, right, lag), (test_shape_function, trial_shape_function, lagrangle_shape_function)):
        for i in range(value_dimension):
            n[i, i::value_dimension] = v[0] if load_validity[i] else 0
    mat = lag.transpose().dot(right) + left.transpose().dot(lag)
    mat *= data['weight']
    vec = lag.transpose().dot(data['load']) * data['weight']
    applyDiagConvention(data, mat)
    return (mat, vec.reshape((vec.shape[0], 1))) 

def applyDiagConvention(data, mat):
    value_dimension = data['valueDimension']
    load_validity = data['loadValidity']    
    for indes in data['lagrangleAssemblyIndes']:
        for d in range(value_dimension):
            if load_validity[d]:
                mat[value_dimension * indes + d][value_dimension * indes + d] = -1;
    
if __name__ == "__main__":
    test_data = gen_test_datas()
    print(common_tools.to_json_string(test_data))
