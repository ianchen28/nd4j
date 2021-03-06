package org.nd4j.autodiff;

import com.google.common.base.Preconditions;
import lombok.*;
import org.nd4j.autodiff.functions.DifferentialFunction;
import org.nd4j.autodiff.graph.Graph;
import org.nd4j.autodiff.opstate.NDArrayInformation;
import org.nd4j.autodiff.opstate.NDArrayVertex;
import org.nd4j.autodiff.opstate.OpState;
import org.nd4j.linalg.api.ops.impl.accum.*;
import org.nd4j.linalg.api.ops.impl.accum.distances.CosineSimilarity;
import org.nd4j.linalg.api.ops.impl.accum.distances.EuclideanDistance;
import org.nd4j.linalg.api.ops.impl.accum.distances.ManhattanDistance;
import org.nd4j.linalg.api.ops.impl.scalar.*;
import org.nd4j.linalg.api.ops.impl.transforms.*;
import org.nd4j.linalg.api.ops.impl.transforms.arithmetic.*;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.lossfunctions.impl.*;
import org.nd4j.linalg.util.ArrayUtil;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by agibsonccc on 4/4/17.
 */
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class ArrayField implements Field<ArrayField> {
    @Getter
    @Setter
    private Graph<NDArrayInformation,OpState> ops;
    @Getter
    @Setter
    private NDArrayInformation input;
    @Getter
    @Setter
    private NDArrayVertex vertex;

    public ArrayField(NDArrayVertex ndArrayVertex,
                      Graph<NDArrayInformation,OpState> ops) {
        this.input = ndArrayVertex.getValue();
        this.vertex = ndArrayVertex;
        this.ops = ops;
    }


    @Override
    public ArrayField negate() {
        return addTransformOp(new Negative().name());
    }

    @Override
    public ArrayField add(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarAdd().name(),getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),false);

        return addPairTransformOp(new AddOp().name(),i_v);
    }



    @Override
    public ArrayField sub(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 || ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarSubtraction().name(),getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),false);
        return addPairTransformOp(new SubOp().name(),i_v);
    }

    @Override
    public ArrayField rsub(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarReverseSubtraction().name(),
                    getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),false);
        return addPairTransformOp("rsub",i_v);
    }

    @Override
    public ArrayField mul(double i_n) {
        return addScalarTransformOp(new ScalarMultiplication().name(),i_n);
    }

    @Override
    public ArrayField sub(double i_v) {
        return addScalarTransformOp("sub",i_v);
    }

    @Override
    public ArrayField negatei() {
        return addTransformOp(new Negative().name(),new Object[]{true});
    }

    @Override
    public ArrayField addi(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 || ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarAdd().name(),getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),true);
        return addPairTransformOp(new AddOp().name(),i_v,new Object[]{true});
    }

    @Override
    public ArrayField addi(double i_v) {
        return addScalarTransformOp(new ScalarAdd().name(),input,getInput().getShape(),i_v,true);
    }

    @Override
    public ArrayField muli(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarMultiplication().name(),getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),true);
        return addPairTransformOp(new MulOp().name(),i_v,new Object[]{true});
    }

    @Override
    public ArrayField muli(double v) {
        return addScalarTransformOp(new ScalarMultiplication().name(),input,getInput().getShape(),v,true);
    }

    @Override
    public ArrayField powi(int i_n) {
        return null;
    }

    @Override
    public ArrayField mul(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarMultiplication().name(),getNonScalar(i_v),getNonScalarShape(i_v),getScalar(i_v),false);
        return addPairTransformOp(new MulOp().name(),i_v);
    }

    @Override
    public ArrayField pow(int i_n) {
        return addScalarTransformOp(new Pow().name(),i_n);
    }

    @Override
    public ArrayField inverse() {
        //   return new ArrayField(InvertMatrix.invert(input,false)),ops);
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayField rsubi(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new RSubOp().name(),
                    getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),
                    true);
        return addPairTransformOp(new RSubOp().name(),i_v,new Object[]{true});
    }

    @Override
    public ArrayField rdivi(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new RDivOp().name(),
                    getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),
                    true);
        return addPairTransformOp(new RDivOp().name(),i_v,new Object[]{true});
    }

    @Override
    public ArrayField subi(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarSubtraction().name(),
                    getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),
                    true);
        return addPairTransformOp(new SubOp().name(),i_v,new Object[]{true});
    }

    @Override
    public ArrayField divi(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarDivision().name(),getNonScalar(i_v),
                    getNonScalarShape(i_v),getScalar(i_v),true);
        return addPairTransformOp(new DivOp().name(),i_v,new Object[]{true});
    }

    @Override
    public ArrayField inversei() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayField subi(double i_v) {
        return addScalarTransformOp(new ScalarSubtraction().name(),input,getInput().getShape(),i_v,true);
    }

    @Override
    public ArrayField rsubi(double v) {
        return addScalarTransformOp(new ScalarReverseSubtraction().name(),input,getInput().getShape(),v,true);
    }

    @Override
    public ArrayField rdivi(double v) {
        return addScalarTransformOp(new ScalarReverseDivision().name(),input,getInput().getShape(),v,true);
    }

    @Override
    public ArrayField divi(double v) {
        return addScalarTransformOp(new ScalarDivision().name(),input,getInput().getShape(),v,true);
    }

    @Override
    public ArrayField rdiv(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 ||
                ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarReverseDivision().name(),getNonScalar(i_v),getNonScalarShape(i_v),getScalar(i_v),false);
        return addPairTransformOp("rdiv",i_v);
    }

    @Override
    public ArrayField div(ArrayField i_v) {
        if(ArrayUtil.prod(i_v.getInput().getShape()) == 1 || ArrayUtil.prod(getInput().getShape()) == 1)
            return addScalarTransformOp(new ScalarDivision().name(),getNonScalar(i_v),getNonScalarShape(i_v),getScalar(i_v),false);
        return addPairTransformOp(new DivOp().name(),i_v);
    }

    @Override
    public double getReal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayField[] args() {
        return new ArrayField[0];
    }

    @Override
    public ArrayField rsub(double v) {
        return addScalarTransformOp("rsub",v);
    }

    @Override
    public ArrayField rdiv(double v) {
        return addScalarTransformOp("rdiv",v);
    }

    @Override
    public ArrayField pow(ArrayField a) {
        return addPairTransformOp(new Pow().name(),a);
    }

    @Override
    public ArrayField floor() {
        return addTransformOp(new Floor().name());
    }

    @Override
    public ArrayField ceil() {
        return addTransformOp(new Ceil().name());
    }

    @Override
    public ArrayField round() {
        return addTransformOp(new Round().name());
    }

    @Override
    public ArrayField abs() {
        return addTransformOp(new Abs().name());
    }


    @Override
    public ArrayField sqrt() {
        return addTransformOp(new Sqrt().name());
    }
    // Operators for double
    @Override
    public ArrayField add(double v) {
        return addScalarTransformOp(new ScalarAdd().name(),v);
    }

    @Override
    public ArrayField minus(double v) {
        return addScalarTransformOp(new ScalarSubtraction().name(),v);
    }

    @Override
    public ArrayField prod(double v) {
        return addScalarTransformOp(new ScalarMultiplication().name(),v);
    }

    @Override
    public ArrayField div(double v) {
        return addScalarTransformOp(new ScalarDivision().name(),v);
    }

    @Override
    public ArrayField pow(double v) {
        return addScalarTransformOp(new Pow().name(),v);
    }

    @Override
    public ArrayField cos() {
        return addTransformOp(new Cos().name());
    }

    @Override
    public ArrayField acos() {
        return addTransformOp(new ACos().name());
    }

    @Override
    public ArrayField cosh() {
        return addTransformOp(new Cosh().name());
    }

    @Override
    public ArrayField acosh() {
        //  return new ArrayField(OpState.fromOp(new INDArray(Math.log(Math.sqrt(Math.pow(x, 2) - 1) + x)),ops);
        throw new UnsupportedOperationException();

    }

    @Override
    public ArrayField sin() {
        return addTransformOp(new Sin().name());
    }

    @Override
    public ArrayField asin() {
        return addTransformOp(new ASin().name());
    }

    @Override
    public ArrayField sinh() {
        return addTransformOp(new Sinh().name());
    }

    @Override
    public ArrayField asinh() {
        //  return new ArrayField(OpState.fromOp(new INDArray(Math.log(Math.sqrt(Math.pow(x, 2) + 1) + x)),ops);
        throw new UnsupportedOperationException();

    }

    @Override
    public ArrayField tan() {
        return addTransformOp(new Tan().name());
    }

    @Override
    public ArrayField atan() {
        return addTransformOp(new ATan().name());
    }

    @Override
    public ArrayField tanh() {
        return addTransformOp(new Tanh().name());
    }

    @Override
    public ArrayField atanh() {
        return addTransformOp(new ATanh().name());
    }

    @Override
    public ArrayField exp() {
        return addTransformOp(new Exp().name());
    }

    @Override
    public ArrayField log() {
        return addTransformOp(new Log().name());
    }

    @Override
    public ArrayField log10() {
        //return new ArrayField(OpState.fromOp(new INDArray(Math.log10(x)),ops);
        throw new UnsupportedOperationException();

    }

    @Override
    public ArrayField sgn() {
        return addTransformOp(new Sign().name());
    }

    @Override
    public ArrayField pwr(ArrayField y) {
        //return new ArrayField(OpState.fromOp(new INDArray(Math.pow(Math.abs(x)), y.doubleValue())),ops);
        throw new UnsupportedOperationException();
    }

    @Override
    public ArrayField pwrs(ArrayField y) {
        // return new ArrayField(OpState.fromOp(new INDArray(Math.pow(Math.abs(x)), y.doubleValue()) * Math.signum(x)),ops);
        throw new UnsupportedOperationException();

    }

    @Override
    public ArrayField square() {
        return mul(this);
    }

    @Override
    public ArrayField relu() {
        return addTransformOp(new RectifedLinear().name());
    }

    @Override
    public ArrayField hardTanh() {
        return addTransformOp(new HardTanh().name());
    }

    @Override
    public ArrayField hardTanhDerivative() {
        return addTransformOp(new HardTanhDerivative().name());
    }

    @Override
    public ArrayField leakyRelu() {
        return addTransformOp(new LeakyReLU().name());
    }

    @Override
    public ArrayField elu() {
        return addTransformOp(new ELU().name());
    }

    @Override
    public ArrayField eluDerivative() {
        return addTransformOp(new ELUDerivative().name());
    }


    @Override
    public ArrayField leakyRelu(double cutoff)  {
        return addTransformOp(new LeakyReLU().name(),new Object[]{cutoff});
    }

    @Override
    public ArrayField leakyReluDerivative() {
        return addTransformOp(new LeakyReLUDerivative().name());
    }

    @Override
    public ArrayField leakyReluDerivative(double cutoff)  {
        return addTransformOp(new LeakyReLUDerivative().name(),new Object[]{cutoff});
    }


    @Override
    public ArrayField sigmoid() {
        return addTransformOp(new Sigmoid().name());
    }

    @Override
    public ArrayField sigmoidDerivative() {
        return addTransformOp(new SigmoidDerivative().name());
    }

    @Override
    public ArrayField step() {
        return addTransformOp(new Step().name());
    }

    @Override
    public ArrayField softsign() {
        return addTransformOp(new SoftSign().name());
    }

    @Override
    public ArrayField softsignDerivative() {
        return addTransformOp(new LeakyReLUDerivative().name());
    }

    @Override
    public ArrayField softmax() {
        return addTransformOp(new SoftMax().name());
    }

    @Override
    public ArrayField softplus() {
        return addTransformOp(new SoftPlus().name());
    }

    @Override
    public ArrayField reshape(int[] shape) {
        return addTransformOp("reshape",new Object[]{shape});
    }

    @Override
    public ArrayField transpose() {
        return addArrayOp(
                "transpose",
                null,
                ArrayUtil.reverseCopy(input.getShape()),
                null,
                OpState.OpType.SHAPE);
    }

    @Override
    public ArrayField permute(int[] dimensions) {
        return addArrayOp(
                "permute",
                null,
                ArrayUtil.permute(input.getShape(),dimensions),
                null,
                OpState.OpType.SHAPE);

    }

    @Override
    public ArrayField expandDims(int dim) {
        return addArrayOp(
                "expandDims",
                new int[]{dim},
                ArrayUtil.reverseCopy(input.getShape()),
                null,
                OpState.OpType.SHAPE);
    }

    @Override
    public ArrayField sum(int[] dimensions) {
        return addArrayOp(
                new Sum().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                null,
                OpState.OpType.ACCUMULATION);
    }

    @Override
    public ArrayField prod(int[] dimensions) {
        return addArrayOp(
                new Prod().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                null,
                OpState.OpType.ACCUMULATION);
    }

    @Override
    public ArrayField mean(int[] dimensions) {
        return addArrayOp(
                new Mean().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                null,
                OpState.OpType.ACCUMULATION);
    }


    @Override
    public ArrayField std(int[] dimensions,boolean biasCorrected) {
        return addArrayOp(
                new StandardDeviation().name()
                ,dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                new Object[]{biasCorrected},
                OpState.OpType.ACCUMULATION);
    }

    @Override
    public ArrayField variance(int[] dimensions,boolean biasCorrected) {
        return addArrayOp(
                new Variance().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                new Object[]{biasCorrected},
                OpState.OpType.ACCUMULATION);
    }

    @Override
    public ArrayField std(int[] dimensions) {
        return std(dimensions,false);
    }

    @Override
    public ArrayField variance(int[] dimensions) {
        return variance(dimensions,false);
    }

    @Override
    public ArrayField max(int[] dimensions) {
        return addArrayOp(
                new Max().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                null,
                OpState.OpType.ACCUMULATION);
    }

    @Override
    public ArrayField min(int[] dimensions) {
        return addArrayOp(
                new Min().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                null,
                OpState.OpType.ACCUMULATION);
    }

    @Override
    public ArrayField norm1(int[] dimensions) {
        return addArrayOp(
                new Norm1().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                null,
                OpState.OpType.ACCUMULATION);
    }

    @Override
    public ArrayField norm2(int[] dimensions) {
        return addArrayOp(
                new Norm2().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                null,
                OpState.OpType.ACCUMULATION);
    }

    @Override
    public ArrayField normmax(int[] dimensions) {
        return addArrayOp(
                new NormMax().name(),
                dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                null,
                OpState.OpType.ACCUMULATION);
    }


    @Override
    public ArrayField valueArrayOf(int[] shape) {
        return addArrayOp(
                "full",
                null,
                shape,
                null,
                OpState.OpType.BROADCAST);
    }



    @Override
    public ArrayField tile(int[] repeat) {
        return addArrayOp(
                "tile",
                null,
                null,
                new Object[]{repeat},
                OpState.OpType.BROADCAST);
    }


    @Override
    public ArrayField repeat(int axis) {
        return addArrayOp("repeat",
                new int[]{axis},
                input.getShape(),
                null,
                OpState.OpType.BROADCAST);
    }

    @Override
    public ArrayField broadcast(int[] shape) {
        return addArrayOp("broadcast",null,shape,null, OpState.OpType.BROADCAST);
    }


    @Override
    public ArrayField eq(ArrayField i_y) {
        return addPairTransformOp(new EqualsWithEps().name(),i_y);
    }

    @Override
    public ArrayField neq(ArrayField i_y) {
        return addPairTransformOp(new Not().name(),i_y);
    }

    @Override
    public ArrayField or(ArrayField i_y) {
        return addPairTransformOp(new Or().name(),i_y);
    }

    @Override
    public ArrayField rollAxis(int axis) {
        return addTransformOp("rollAxis",new Object[]{axis});
    }

    @Override
    public ArrayField cosineSimilarity(ArrayField i_y, int...dimensions) {
        return addPairReduceOp(new CosineSimilarity().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField euclideanDistance(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new EuclideanDistance().name(),i_y,dimensions,null);

    }

    @Override
    public ArrayField manhattanDistance(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new ManhattanDistance().name(),i_y,dimensions,null);

    }

    @Override
    public ArrayField lossBinaryXENT(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossBinaryXENT().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField lossCosineSimilarity(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossCosineProximity().name(),i_y,dimensions,null);

    }

    @Override
    public ArrayField lossHinge(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossHinge().name(),i_y,dimensions,null);

    }

    @Override
    public ArrayField lossKLD(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossKLD().name(),i_y,dimensions,null);
    }


    @Override
    public ArrayField lossL1(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossL1().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField lossL2(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new CosineSimilarity().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField lossMAE(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossMAE().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField lossMAPE(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossMAPE().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField lossMSE(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossMSE().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField lossMCXENT(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossMCXENT().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField lossMSLE(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossMSLE().name(),i_y,dimensions,null);
    }

    @Override
    public ArrayField lossNegativeLogLikelihood(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossNegativeLogLikelihood().name(),i_y,dimensions,null);
    }


    @Override
    public ArrayField lossPoisson(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossPoisson().name(),i_y,dimensions,null);
    }


    @Override
    public ArrayField lossSquaredHinge(ArrayField i_y,int...dimensions) {
        return addPairReduceOp(new LossSquaredHinge().name(),i_y,dimensions,null);
    }

    @Override
    public DifferentialFunction arg() {
        throw new UnsupportedOperationException();
    }


    private ArrayField addTransformOp(String name) {
        return addTransformOp(name,null,null);
    }


    private ArrayField addFirstScalarTransformOp(String name,
                                                 ArrayField i_v,
                                                 Object[] extraArgs) {
        Preconditions.checkState(this.ops == i_v.ops, "If adding a field. Must be apart of the same graph.");

        NDArrayInformation ndArrayInformation =  NDArrayInformation.builder()
                .id(name + "(" + input.getId() + ")").scalarValue(this.getInput().getScalarValue())
                .arrId(UUID.randomUUID().toString())
                .shape(getInput().getShape()).build();
        //result
        NDArrayVertex newVertex = new NDArrayVertex(
                this.ops.nextVertexId(),
                ndArrayInformation);

        //add the result vertex to the graph
        this.getOps().addVertex(newVertex);

        OpState owner =    OpState.builder()
                .n(ArrayUtil.prod(getInput().getShape()))
                .opName(name).extraArgs(extraArgs)
                .scalarValue(getInput().scalar())
                .id(vertex.getValue().getId() + "-> " + name + " " + newVertex.getValue().getId())
                .opType(OpState.OpType.SCALAR_TRANSFORM).result(newVertex.getValue())
                .vertexIds(new String[]{String.valueOf(vertex.vertexID()),String.valueOf(newVertex.vertexID())})
                .build();

        //map x -> z
        this.ops.addEdge(vertex.vertexID(),
                newVertex.vertexID(),owner
                ,true);

        ndArrayInformation.setOwner(owner);
        if(owner.isInPlace()) {
            ndArrayInformation.setArrId(input.getArrId());
        }
        return new ArrayField(newVertex,ops);
    }

    private ArrayField addScalarTransformOp(String name,
                                            NDArrayInformation input,
                                            int[] nonScalarShape,
                                            Number scalarValue,

                                            boolean inPlace) {
       //for the purpose of the graph, we only need the scalar
        //value, therefore the input should be the
        //non scalar


       if(this.getInput() != input) {
           setInput(input);
           getVertex().setValue(input);
       }


        NDArrayInformation result =  NDArrayInformation.builder()
                .scalarValue(scalarValue)
                .id(name + "(" + input.getId() + ")")
                .arrId(UUID.randomUUID().toString())
                .shape(nonScalarShape).build();
        //result
        NDArrayVertex newVertex = new NDArrayVertex(
                this.ops.nextVertexId(),
                result);

        //add the result vertex to the graph
        this.getOps().addVertex(newVertex);

        OpState owner =  OpState.builder()
                .n(ArrayUtil.prod(input.getShape()))
                .opName(name).extraArgs(new Object[]{scalarValue,inPlace})
                .scalarValue(scalarValue)
                .id(vertex.getValue().getId() + "-> " +
                        name + " " + newVertex.getValue().getId())
                .opType(OpState.OpType.SCALAR_TRANSFORM)
                .result(newVertex.getValue())
                .vertexIds(new String[]{String.valueOf(vertex.vertexID()),
                        String.valueOf(newVertex.vertexID())})
                .build();
        //map x -> z
        this.ops.addEdge(vertex.vertexID(),
                newVertex.vertexID(), owner,true);
        result.setOwner(owner);
        if(owner.isInPlace()) {
            result.setArrId(input.getArrId());
        }
        return new ArrayField(newVertex,ops);
    }

    private ArrayField addScalarTransformOp(String name,Number scalarValue) {
        return addScalarTransformOp(name,input,input.getShape(),scalarValue,false);
    }

    private ArrayField addPairReduceOp(String name,
                                       ArrayField i_v,
                                       int[] dimensions,
                                       Object[] extraArgs) {
        return addPairReduceOp(name,
                i_v,dimensions,
                Shape.getReducedShape(input.getShape(),dimensions),
                extraArgs);
    }

    private ArrayField addPairReduceOp(String name,ArrayField i_v,
                                       int[] dimensions,
                                       int[] resultShape,
                                       Object[] extraArgs) {
        Preconditions.checkState(this.ops == i_v.ops, "If adding a field. Must be apart of the same graph.");

        NDArrayInformation information =   NDArrayInformation.builder()
                .id(name + "("+ getVertex().getValue().getId() + "," + i_v.getVertex().getValue().getId() + ")")
                .arrId(UUID.randomUUID().toString())
                .shape(resultShape).build();

        NDArrayVertex newVertex = new NDArrayVertex(this.ops.nextVertexId(), information);

        //add the result vertex to the graph
        this.getOps().addVertex(newVertex);

        //map x -> z
        OpState xToz = OpState.builder()
                .n(ArrayUtil.prod(resultShape))
                .opName(name).extraArgs(extraArgs)
                .id(vertex.getValue().getId() + "-> " + name + " " + newVertex.getValue().getId())
                .vertexIds(new String[]{String.valueOf(vertex.vertexID()),String.valueOf(newVertex.vertexID())})
                .opType(OpState.OpType.ACCUMULATION).build();
        xToz.setResult(information);
        this.ops.addEdge(vertex.vertexID(),
                newVertex.vertexID(),xToz,true);
        //map y -> z
        OpState yToZ = OpState.builder()
                .n(ArrayUtil.prod(resultShape))
                .opName(name).extraArgs(extraArgs)
                .id(i_v.getVertex().getValue().getId() + "-> " + name + " " + newVertex.getValue().getId())
                .vertexIds(new String[]{String.valueOf(i_v.getVertex().vertexID()),String.valueOf(newVertex.vertexID())})
                .opType(OpState.OpType.ACCUMULATION).build();
        yToZ.setResult(information);

        if(xToz.isInPlace()) {
            information.setArrId(input.getArrId());
        }

        this.ops.addEdge(i_v.getVertex().vertexID(),
                newVertex.vertexID(),yToZ,true);

        return new ArrayField(newVertex,ops);
    }



    private ArrayField addPairReduceOp(String name,
                                       ArrayField i_v,
                                       Object[] extraArgs) {

        Preconditions.checkState(this.ops == i_v.ops, "If adding a field. Must be apart of the same graph.");

        //result
        NDArrayInformation resultInfo =  NDArrayInformation.builder().arrId(UUID.randomUUID().toString())
                .id(name + "("+ getVertex().getValue().getId() + "," + i_v.getVertex().getValue().getId() + ")")
                .shape(input.getShape()).build();
        NDArrayVertex newVertex = new NDArrayVertex(this.ops.nextVertexId(), resultInfo);

        //add the result vertex to the graph
        this.getOps().addVertex(newVertex);

        //map x -> z
        OpState xToZ = OpState.builder()
                .n(ArrayUtil.prod(input.getShape()))
                .opName(name).extraArgs(extraArgs)
                .id(vertex.getValue().getId() + "-> " + name + " " + newVertex.getValue().getId())
                .vertexIds(new String[]{String.valueOf(vertex.vertexID()),String.valueOf(newVertex.vertexID())})
                .opType(OpState.OpType.ACCUMULATION).build();
        xToZ.setResult(resultInfo);
        this.ops.addEdge(vertex.getIdx(),
                newVertex.vertexID(),xToZ,true);
        //map y -> z
        OpState yToZ = OpState.builder()
                .n(ArrayUtil.prod(input.getShape()))
                .opName(name).extraArgs(extraArgs)
                .id(i_v.getVertex().getValue().getId() + "-> " + name + " " + newVertex.getValue().getId())
                .vertexIds(new String[]{String.valueOf(i_v.getVertex().vertexID()),String.valueOf(newVertex.vertexID())})
                .opType(OpState.OpType.ACCUMULATION).build();
        yToZ.setResult(resultInfo);
        this.ops.addEdge(i_v.getVertex().getIdx(),
                newVertex.vertexID(),yToZ,true);
        resultInfo.setOwner(yToZ);

        if(xToZ.isInPlace()) {
            resultInfo.setArrId(input.getArrId());
        }


        return new ArrayField(newVertex,ops);
    }


    private ArrayField addPairTransformOp(String name,ArrayField i_v,Object[] extraArgs) {
        if(ArrayUtil.prod(getInput().getShape()) == 1) {
            return addFirstScalarTransformOp(name + "_scalar",
                    i_v,extraArgs);
        }

        Preconditions.checkState(this.ops == i_v.ops, "If adding a field. Must be apart of the same graph.");
        //result
        NDArrayInformation resultInfo =  NDArrayInformation.builder().arrId(UUID.randomUUID().toString())
                .id(name + "("+ getVertex().getValue().getId() + "," + i_v.getVertex().getValue().getId() + ")")
                .shape(input.getShape()).build();
        NDArrayVertex newVertex = new NDArrayVertex(this.ops.nextVertexId(), resultInfo);

        Preconditions.checkArgument(Arrays.equals(input.getShape(),i_v.getInput().getShape()),"X and y not equal shapes.");

        //add the result vertex to the graph
        this.getOps().addVertex(newVertex);

        //map x -> z
        OpState xToZ = OpState.builder()
                .n(ArrayUtil.prod(input.getShape()))
                .opName(name).extraArgs(extraArgs)
                .id(vertex.getValue().getId() + "-> " + name + " " + newVertex.getValue().getId())
                .vertexIds(new String[]{String.valueOf(vertex.vertexID()),String.valueOf(newVertex.vertexID())})
                .opType(OpState.OpType.TRANSFORM).build();
        xToZ.setResult(resultInfo);
        if(vertex.vertexID() == newVertex.vertexID())
            throw new IllegalStateException("Attempted to add edge with vertex id of " + newVertex.vertexID() +
                    " when next vertex id was " + this.ops.getNextVertexId() + " . This usually means that the vertex id generation was behind the nodes being added.");

        this.ops.addEdge(vertex.vertexID(),
                newVertex.vertexID(),xToZ,true);
        //map y -> z
        OpState yToZ = OpState.builder()
                .n(ArrayUtil.prod(input.getShape()))
                .opName(name).extraArgs(extraArgs)
                .id(i_v.getVertex().getValue().getId() + "-> " + name + " " + newVertex.getValue().getId())
                .vertexIds(new String[]{String.valueOf(i_v.getVertex().vertexID()),String.valueOf(newVertex.vertexID())})
                .opType(OpState.OpType.TRANSFORM).build();
        yToZ.setResult(resultInfo);
        if(i_v.getVertex().vertexID() == newVertex.vertexID())
            throw new IllegalStateException("Attempted to add edge with vertex id of " + newVertex.vertexID() +
                    " when next vertex id was " + this.ops.getNextVertexId() + " . This usually means that the vertex id generation was behind the nodes being added.");
        this.ops.addEdge(i_v.getVertex().vertexID(),
                newVertex.vertexID(),yToZ,true);
        resultInfo.setOwner(yToZ);

        if(xToZ.isInPlace()) {
            resultInfo.setArrId(input.getArrId());
        }

        return new ArrayField(newVertex,ops);
    }

    private ArrayField  addPairTransformOp(String name,ArrayField i_v) {
        return addPairTransformOp(name,i_v,null);
    }

    private ArrayField addTransformOp(String name,Object[] extraArgs) {
        return addTransformOp(name,null,extraArgs);
    }

    private ArrayField addTransformOp(String name,int[] axes,Object[] extraArgs) {
        return addArrayOp(name,
                axes,extraArgs,
                OpState.OpType.TRANSFORM);
    }




    private NDArrayVertex getVertex(String name,int[] shape) {
        //result
        NDArrayVertex newVertex = new NDArrayVertex(this.ops.nextVertexId() ,
                NDArrayInformation.builder().arrId(UUID.randomUUID().toString())
                        .id(name + "(" + input.getId() + ")")
                        .shape(shape).build());
        return newVertex;

    }

    private ArrayField addArrayOp(String name,
                                  int[] axes,
                                  Object[] extraArgs,
                                  OpState.OpType opType) {
        return addArrayOp(name,
                axes,
                input.getShape(),
                extraArgs,
                opType);
    }

    private ArrayField addArrayOp(String name,
                                  int[] axes,
                                  int[] shape,
                                  Object[] extraArgs,
                                  OpState.OpType opType) {
        //result
        NDArrayVertex newVertex = getVertex(name,shape);
        //add the result vertex to the graph
        this.getOps().addVertex(newVertex);

        OpState opState = OpState.builder()
                .n(ArrayUtil.prod(input.getShape()))
                .opName(name).extraArgs(extraArgs).axes(axes)
                .result(newVertex.getValue())
                .id(vertex.getValue().getId() + "-> " + name + " " + newVertex.getValue().getId())
                .vertexIds(new String[]{String.valueOf(vertex.vertexID()),String.valueOf(newVertex.vertexID())})
                .opType(opType).build();

        if(opState.isInPlace()) {
            newVertex.getValue().setArrId(input.getArrId());
        }
        //map x -> z
        this.ops.addEdge(vertex.vertexID(),
                newVertex.vertexID(),opState,true);

        return new ArrayField(newVertex,ops);
    }


    private double getScalar(ArrayField other) {
        if(ArrayUtil.prod(getInput().getShape()) == 1)
            return this.getInput().getScalarValue().doubleValue();
        else if(ArrayUtil.prod(other.getInput().getShape()) == 1)
            return other.getInput().getScalarValue().doubleValue();
        throw new IllegalArgumentException("Neither this element nor the other input is a scalar");
    }

    private NDArrayInformation getNonScalar(ArrayField other) {
        if(ArrayUtil.prod(getInput().getShape()) != 1 && ArrayUtil.prod(other.getInput().getShape()) == 1)
            return this.getInput();
        else if(ArrayUtil.prod(other.getInput().getShape()) != 1 && ArrayUtil.prod(getInput().getShape()) == 1)
            return other.getInput();
        throw new IllegalArgumentException("Neither this element nor the other input is a scalar");

    }
    private int[] getNonScalarShape(ArrayField other) {
        if(ArrayUtil.prod(getInput().getShape()) != 1 && ArrayUtil.prod(other.getInput().getShape()) == 1)
            return this.getInput().getShape();
        else if(ArrayUtil.prod(other.getInput().getShape()) != 1 && ArrayUtil.prod(getInput().getShape()) == 1)
            return other.getInput().getShape();
        throw new IllegalArgumentException("Neither this element nor the other input is a scalar");

    }

    @Override
    public String toString() {
        return "ArrayField{" +
                "input=" + input +
                '}';
    }


    public ArrayField mmul(ArrayField value) {
        return addPairReduceOp("mmul",value,
                null,
                Shape.getMatrixMultiplyShape(getInput().getShape(),
                        value.getInput().getShape()),null);
    }

    public ArrayField tensorMmul(DifferentialFunction<ArrayField> y, int[][] dimensions) {
        return addPairReduceOp("tensorMmul",y.getValue(true),
                null,
                ArrayUtil.getTensorMmulShape(getInput().getShape(),
                        y.getValue(true).getInput().getShape(),
                        dimensions),new Object[]{dimensions});

    }


}
