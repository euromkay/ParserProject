import java.util.Vector;



public class FunctionPointerType extends Type {
	
	
	Type returnType;
	
	public FunctionPointerType(Type returnType) {
		super(returnType.getName(), 4);
		this.returnType = returnType;
	}

	private boolean ref;
	public boolean isRef() {
		return ref;
	}
	public void setRef(boolean ref) {
		this.ref = ref;
		
	}
	public int getNumParams(){
		return m_paramList.size();
	}

	public void setParameters(Vector<VarSTO> params) {
		this.m_paramList = params;

		StringBuffer sb = new StringBuffer("funcptr : ");
		sb.append(returnType.getName());
		sb.append(" ");
		if(ref)
			sb.append("& ");
		sb.append("(");
		for(VarSTO v: params){
			sb.append(v.getType().getName());
			sb.append(" ");
			if(v.isRef())
				sb.append("&");
			sb.append(v.getName());
			sb.append(",");
			sb.append(" ");
		}
		
		if(params.size() > 0){
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
		}
		
		sb.append(")");
		setName(sb.toString());
	}

	public VarSTO get(int i) {
		return m_paramList.get(i);
	}

	public Type getReturnType(){
		return returnType;
	}

	private Vector<VarSTO> m_paramList = new Vector<VarSTO>();
	public Vector<VarSTO> getParams(){
		return m_paramList;
	}

	public boolean isAssignable(Type t){
		if(t instanceof NullPointerType){
			return true;
		}
		if(t == null)
			return true;
		if(!(t instanceof FunctionPointerType))
			return false;
		FunctionPointerType ft = (FunctionPointerType) t;
		
		if(!returnType.isEquivalent(ft.returnType))
			return false;
		
		if(m_paramList.size() != ft.getNumParams())
			return false;
		
		for(int i = 0; i < ft.getNumParams(); i++){
			VarSTO v1 = m_paramList.get(i);
			VarSTO v2 = ft.m_paramList.get(i);
			
			if(v1.isRef() != v2.isRef())
				return false;
			
			if(!v1.getType().isEquivalent(v2.getType()))
				return false;
		}
		
		return true;
	}
	
	public boolean isEquivalent (Type t){
		return isAssignable(t);
	}
	public void setReturnType(Type _1) {
		returnType = _1;
		
	}
	

}
