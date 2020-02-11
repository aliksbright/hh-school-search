package HHSchoolSearch.Search.QueryStructure;

public class QBinOperation extends QOperand
{
    private QBinOperator oper;
    private QOperand left;
    private QOperand right;

    public QBinOperation(QBinOperator _oper, QOperand _left, QOperand _right)
    {
        setOper(_oper);
        setLeft(_left);
        setRight(_right);
    }

    public QBinOperator getOper() { return oper; }

    public void setOper(QBinOperator oper) { this.oper = oper; }

    public QOperand getLeft() { return left; }

    public void setLeft(QOperand left) { this.left = left; }

    public QOperand getRight() { return right; }

    public void setRight(QOperand right) { this.right = right; }
}