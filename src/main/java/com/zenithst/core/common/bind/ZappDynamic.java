package com.zenithst.core.common.bind;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to create dynamic conditions <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappDynamic extends ZappDomain {

    public String orderByClause;
    protected boolean distinct;
    protected List<Criteria> oredCriteria;

    public ZappDynamic() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        /* Alias */
        protected String alias;
        /* Column */
        protected String column;
        /* All */
        protected String whole;
        
        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }
        
        /* Alias */
        public void setAlias(String alias) {
        	this.alias = ZstFwValidatorUtils.fixNullString(alias, BLANK, BLANK, PERIOD);
        }
        
        /* Column */
        public void setColumn(String column) {
        	this.column = ZstFwValidatorUtils.fixNullString(column, BLANK).toUpperCase();
        	this.whole = this.alias + this.column;
        }

        /* NULL */
        public Criteria andIsNull() {
            addCriterion(whole + " is null");
            return (Criteria) this;
        }

        /* IS NOT NULL */
        public Criteria andIsNotNull() {
            addCriterion(whole + " is not null");
            return (Criteria) this;
        }
        
        /* EQUAL */
        public Criteria andEqualTo(Object value) {
        	if(value instanceof String) {
        		addCriterion(whole + " = ", (String) value, column.toLowerCase());
        	}
        	else if(value instanceof Integer) {
        		addCriterion(whole + " = ", (Integer) value, column.toLowerCase());
        	}
        	else if(value instanceof BigDecimal) {
        		addCriterion(whole + " = ", (BigDecimal) value, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* NOT EQUAL */
        public Criteria andNotEqualTo(Object value) {
            if(value instanceof String) {
        		addCriterion(whole + " <> ", (String) value, column.toLowerCase());
        	}
        	else if(value instanceof Integer) {
        		addCriterion(whole + " <> ", (Integer) value, column.toLowerCase());
        	}
        	else if(value instanceof BigDecimal) {
        		addCriterion(whole + " <> ", (BigDecimal) value, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* GREATER THAN */
        public Criteria andGreaterThan(Object value) {
        	if(value instanceof String) {
        		addCriterion(whole + " > ", (String) value, column.toLowerCase());
        	}
        	else if(value instanceof Integer) {
        		addCriterion(whole + " > ", (Integer) value, column.toLowerCase());
        	}
        	else if(value instanceof BigDecimal) {
        		addCriterion(whole + " > ", (BigDecimal) value, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* GREATER THAN OR EQUAL */
        public Criteria andGreaterThanOrEqualTo(Object value) {
        	if(value instanceof String) {
        		addCriterion(whole + " >= ", (String) value, column.toLowerCase());
        	}
        	else if(value instanceof Integer) {
        		addCriterion(whole + " >= ", (Integer) value, column.toLowerCase());
        	}
        	else if(value instanceof BigDecimal) {
        		addCriterion(whole + " >= ", (BigDecimal) value, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* LESS THAN */
        public Criteria andLessThan(Object value) {
        	if(value instanceof String) {
        		addCriterion(whole + " < ", (String) value, column.toLowerCase());
        	}
        	else if(value instanceof Integer) {
        		addCriterion(whole + " < ", (Integer) value, column.toLowerCase());
        	}
        	else if(value instanceof BigDecimal) {
        		addCriterion(whole + " < ", (BigDecimal) value, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* LESS THAN OR EQUAL */
        public Criteria andLessThanOrEqualTo(Object value) {
        	if(value instanceof String) {
        		addCriterion(whole + " <= ", (String) value, column.toLowerCase());
        	}
        	else if(value instanceof Integer) {
        		addCriterion(whole + " <= ", (Integer) value, column.toLowerCase());
        	}
        	else if(value instanceof BigDecimal) {
        		addCriterion(whole + " <= ", (BigDecimal) value, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* LIKE */
        public Criteria andLike(Object value) {
        	if(value instanceof String) {
        		addCriterion(whole + " like ", (String) value, column.toLowerCase());
        	}
        	else if(value instanceof Integer) {
        		addCriterion(whole + " like ", (Integer) value, column.toLowerCase());
        	}
        	else if(value instanceof BigDecimal) {
        		addCriterion(whole + " like ", (BigDecimal) value, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* NOT LIKE */
        public Criteria andNotLike(Object value) {
        	if(value instanceof String) {
        		addCriterion(whole + " not like ", (String) value, column.toLowerCase());
        	}
        	else if(value instanceof Integer) {
        		addCriterion(whole + " not like ", (Integer) value, column.toLowerCase());
        	}
        	else if(value instanceof BigDecimal) {
        		addCriterion(whole + " not like ", (BigDecimal) value, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* IN */
        @SuppressWarnings({ "rawtypes", "unchecked" })
		public Criteria andIn(Object values) {
        	
        	boolean[] checktype = {false, false, false}; 
        	
        	if(values instanceof List) {
        		for (Object obj : (List) values) {
        			if(obj instanceof String) {
        				checktype[0] = true;
        			}
        			else if(obj instanceof Integer) {
        				checktype[1] = true;
                	}
                	else if(obj instanceof BigDecimal) {
                		checktype[2] = true;
                	}
        		}

        		if(checktype[0] == true) {
    				addCriterion(whole + " in ", (List<String>) values, column.toLowerCase());
    			}
    			if(checktype[1] == true) {
            		addCriterion(whole + " in ", (List<Integer>) values, column.toLowerCase());
            	}
            	if(checktype[2] == true) {
            		addCriterion(whole + " in ", (List<BigDecimal>) values, column.toLowerCase());
            	}

        	}
        	
            return (Criteria) this;
        }

        /* NOT IN */
        @SuppressWarnings({ "rawtypes", "unchecked" })
		public Criteria andNotIn(Object values) {
        	
        	boolean[] checktype = {false, false, false}; 
        	
        	if(values instanceof List) {
        		for (Object obj : (List) values) {
        			if(obj instanceof String) {
        				checktype[0] = true;
        			}
        			else if(obj instanceof Integer) {
        				checktype[1] = true;
                	}
                	else if(obj instanceof BigDecimal) {
                		checktype[2] = true;
                	}
        		}

        		if(checktype[0] == true) {
        			addCriterion(whole + " not in ", (List<String>) values, column.toLowerCase());
    			}
    			if(checktype[1] == true) {
    				addCriterion(whole + " not in ", (List<Integer>) values, column.toLowerCase());
            	}
            	if(checktype[2] == true) {
            		addCriterion(whole + " not in ", (List<BigDecimal>) values, column.toLowerCase());
            	}

        	}

            return (Criteria) this;
        }

        /* BETWEEN */
        public Criteria andBetween(Object value1, Object value2) {
        	if(value1 instanceof String && value2 instanceof String) {
        		addCriterion(whole + " between", (String) value1, (String) value2, column.toLowerCase());
        	}
        	else if(value1 instanceof Integer && value2 instanceof Integer) {
        		addCriterion(whole + " between", (Integer) value1, (Integer) value2, column.toLowerCase());
        	}
        	else if(value1 instanceof BigDecimal && value2 instanceof BigDecimal) {
        		addCriterion(whole + " between", (BigDecimal) value1, (BigDecimal) value2, column.toLowerCase());
        	}
            return (Criteria) this;
        }

        /* NOT BETWEEN */
        public Criteria andNotBetween(Object value1, Object value2) {
        	if(value1 instanceof String && value2 instanceof String) {
        		addCriterion(whole + " not between", (String) value1, (String) value2, column.toLowerCase());
        	}
        	else if(value1 instanceof Integer && value2 instanceof Integer) {
        		addCriterion(whole + " not between", (Integer) value1, (Integer) value2, column.toLowerCase());
        	}
        	else if(value1 instanceof BigDecimal && value2 instanceof BigDecimal) {
        		addCriterion(whole + " not between", (BigDecimal) value1, (BigDecimal) value2, column.toLowerCase());
        	}
            return (Criteria) this;
        }

    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}