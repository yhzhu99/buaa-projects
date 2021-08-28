/*************************************************************************
 * Copyright (C) [2019] by Cambricon, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *************************************************************************/

#include "cnplugin.h"
#include "plugin_power_difference_kernel.h"

typedef uint16_t half;
#if (FLOAT_MODE == 1)
typedef float DType;
#elif (FLOAT_MODE == 0)     // NOLINT
typedef half DType;
#endif

cnmlStatus_t cnmlCreatePluginPowerDifferenceOpParam(
  cnmlPluginPowerDifferenceOpParam_t *param,
  int pow,
  int len,
  cnmlCoreVersion_t core_version
  // TODO：添加变量
) {
  *param = new cnmlPluginPowerDifferenceOpParam();
  // TODO：配置变量
  (*param)->pow = pow;
  (*param)->len = len;
  (*param)->core_version = core_version;
  return CNML_STATUS_SUCCESS;
}

cnmlStatus_t cnmlDestroyPluginPowerDifferenceOpParam(
  cnmlPluginPowerDifferenceOpParam_t *param
) {
  delete (*param);
  *param = nullptr;

  return CNML_STATUS_SUCCESS;
}

cnmlStatus_t cnmlCreatePluginPowerDifferenceOp(
  cnmlBaseOp_t *op,
  cnmlTensor_t *inputs_ptr,
  int input3,
  cnmlTensor_t *outputs_ptr,
  int len
  // TODO：添加变量
) {
  cnrtKernelParamsBuffer_t params;
  cnrtGetKernelParamsBuffer(&params);
  // TODO：配置变量
  cnrtKernelParamsBufferMarkInput(params);   // input 0
  cnrtKernelParamsBufferAddParam(params, &input3, sizeof(int));
  cnrtKernelParamsBufferMarkOutput(params);  // output 0
  cnrtKernelParamsBufferAddParam(params, &len, sizeof(int));
  cnmlCreatePluginOp(op,
                     "PowerDifference",
                     nullptr,
                     params,
                     inputs_ptr,2,
                     outputs_ptr,1,
                     nullptr,0
                     );
  cnrtDestroyKernelParamsBuffer(params);
  return CNML_STATUS_SUCCESS;
}

cnmlStatus_t cnmlComputePluginPowerDifferenceOpForward(
  cnmlBaseOp_t op,
  void **inputs,
  void **outputs,
  // TODO：添加变量
  cnrtQueue_t queue
) {
  // TODO：完成Compute函数
  cnmlComputePluginOpForward_V3(op,
                                inputs,2,
                                outputs,1,
                                nullptr,
                                queue);
  return CNML_STATUS_SUCCESS;
}

