package com.mulesoft.jaxrs.raml.annotation.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class ClassHierarchyVisitor {
	
	public void visit(ITypeModel type, IMethodModel method){
		
		HashSet<String> iSet = new HashSet<String>();
		ArrayList<ITypeModel> iList = new ArrayList<ITypeModel>();
		for(ITypeModel t = type; t!=null ; t = t.getSuperClass()){
			
			if(method!=null?processType(t,method):processType(t)){
				return;
			}
			if(visitInterfaces()){
				processTypeInterfaces(t.getImplementedInterfaces(),iSet,iList);
			}
		}
		for(int i = 0 ; i < iList.size() ; i++){
			ITypeModel t = iList.get(i);
			if(method!=null?processType(t,method):processType(t)){
				return;
			}
            processTypeInterfaces(t.getImplementedInterfaces(),iSet,iList);
		}
	}

	private void processTypeInterfaces(List<ITypeModel> typeInterfaces, HashSet<String> iSet, ArrayList<ITypeModel> iList){
		for(ITypeModel iType : typeInterfaces){
			String name = iType.getFullyQualifiedName();
			if(!iSet.contains(name)){
				iSet.add(name);
				iList.add(iType);
			}
		}
	}

	protected boolean processType(ITypeModel type,IMethodModel method){
		
		IMethodModel[] methods = type.getMethods();
		String methodName = method.getName();
		IParameterModel[] methodParams = method.getParameters();
		int paramsCount = methodParams.length;
		for(IMethodModel m : methods){
			if(m.getName().equals(methodName)){
				IParameterModel[] params = m.getParameters();
				if(params.length==paramsCount){
					boolean match = true;
					for(int i = 0 ; i < paramsCount; i++){
						if(!params[i].getParameterType().equals(methodParams[i].getParameterType())){
							match=false;
							break;
						}
					}
					if(match){
						return checkMethod(m);
					}
				}
			}
		}
		return false;
	}
	
	protected boolean processType(ITypeModel type){
		
		IMethodModel[] methods = type.getMethods();
		for(IMethodModel m : methods){
			if(checkMethod(m)){
				return true;				
			}
		}
		return false;
	}
	
	protected boolean visitInterfaces(){
		return true;
	}

	protected abstract boolean checkMethod(IMethodModel m);

}
