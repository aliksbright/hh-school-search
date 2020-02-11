package HHSchoolSearch.Search.QueryStructure;

import java.util.List;

public class QMultOperation extends QOperand
{
    private QMultOperator oper;
    private List<? extends QOperand> operands;

    public QMultOperation(QMultOperator _oper, List<? extends QOperand> _operands)
    {
        setOper(_oper);
        setOperands(_operands);
    }

    public QMultOperator getOper() { return oper; }

    public void setOper(QMultOperator oper) { this.oper = oper; }

    public List<? extends QOperand> getOperands() { return operands; }

    public void setOperands(List<? extends QOperand> operands) { this.operands = operands; }
}
