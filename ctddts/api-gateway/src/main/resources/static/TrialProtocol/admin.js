
requireRole("ADMIN");

const SEC_TITLES = { dashboard:"Dashboard", users:"User Management", protocols:"Protocols", sites:"Sites" };
document.getElementById("who").textContent = localStorage.getItem("username")||"Admin";


document.querySelectorAll(".nav-item").forEach(b=>b.addEventListener("click",()=>{
  document.querySelectorAll(".nav-item").forEach(x=>x.classList.remove("active"));
  document.querySelectorAll(".sec").forEach(x=>x.classList.remove("active"));
  b.classList.add("active");
  const id=b.dataset.sec;
  document.getElementById(id).classList.add("active");
  document.getElementById("secTitle").textContent=SEC_TITLES[id];
}));

function toggleForm(id){document.getElementById(id).classList.toggle("hidden");}
function badge(s){const m={DRAFT:'b-draft',APPROVED:'b-approved',ACTIVE:'b-active',CLOSED:'b-closed',REGISTERED:'b-approved',SUSPENDED:'b-draft'};return `<span class="badge ${m[s]||'b-draft'}">${pretty(s)}</span>`;}
function pretty(s){return s? s.replace(/_/g," ").replace(/\w\S*/g,w=>w[0]+w.slice(1).toLowerCase()):"";}
function phaseLabel(p){return p? "Phase "+p.replace("PHASE_","") : "";}
function protoLabel(p){return `${p.protocolId} - ${p.trialTitle}`;}
function siteLabel(s) {
  return `${s.siteName} - ${s.siteId}`;
}

let PROTOS=[], SITES=[], USERS=[];

async function createUser(){
  const username = trimVal('u_username');
  const fullName = trimVal('u_fullname');
  const email = trimVal('u_email');
  const password = trimVal('u_password');
  const role = u_role.value;
  if(isBlank(fullName)){
    umsg.innerHTML = '<div class="msg err">Full name is required.</div>';
    return;
  }
  if(isBlank(username)){
    umsg.innerHTML = '<div class="msg err">Username is required and cannot be spaces only.</div>';
    return;
  }
  if(/^\d/.test(username)){
    umsg.innerHTML = '<div class="msg err">Username must not start with a number.</div>';
    return;
  }
  if(/^\d+$/.test(username)){
    umsg.innerHTML = '<div class="msg err">Username cannot be numbers only.</div>';
    return;
  }

  if(isBlank(password)){
    umsg.innerHTML = '<div class="msg err">Password is required and cannot be spaces only.</div>';
    return;
  }
  if(password.length < 8 ||
     !/[A-Z]/.test(password) ||
     !/[a-z]/.test(password) ||
     !/[0-9]/.test(password) ||
     !/[!@#$%^&*]/.test(password)){
    umsg.innerHTML = '<div class="msg err">Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.</div>';
    return;
  }

  if(!role){
    umsg.innerHTML = '<div class="msg err">Role is mandatory.</div>';
    return;
  }

  if(!isCtddtsEmail(email)){
    umsg.innerHTML = '<div class="msg err">Email is required and must use @ctddts.com (e.g. nithish@ctddts.com).</div>';
    return;
  }

  try{
    await api("/users","POST",{username,fullName,email,password,role});
    umsg.innerHTML = '<div class="msg ok">User created</div>';
    u_username.value = u_fullname.value = u_email.value = u_password.value = "";
    loadUsers();
    setTimeout(()=>toggleForm('userForm'),700);
  }catch(e){
    umsg.innerHTML = '<div class="msg err">'+e.message+'</div>';
  }
}

async function deleteUser(id){
  const user=USERS.find(u=>u.id===id);
  if(!user){umsg.innerHTML='<div class="msg err">User not found in current list.</div>';return;}
  if(user.username===localStorage.getItem("username")){umsg.innerHTML='<div class="msg err">You cannot deactivate the account currently signed in.</div>';return;}
  if(!confirm(`Deactivate user "${user.username}"? This will permanently delete the user from the database.`)) return;
  try{await api("/users/"+id,"DELETE");umsg.innerHTML='<div class="msg ok">User deactivated and removed</div>';loadUsers();}
  catch(e){umsg.innerHTML='<div class="msg err">'+e.message+'</div>';}
}
async function createProtocol(){
  const trialTitle=trimVal('p_title');
  if(isBlank(trialTitle)){pmsg.innerHTML='<div class="msg err">Trial Title is required and cannot be spaces only.</div>';return;}
  try{await api("/api/protocols","POST",{trialTitle,therapeuticArea:trimVal('p_area'),phase:p_phase.value,startDate:p_date.value,inclusionCriteria:trimVal('p_incl'),exclusionCriteria:trimVal('p_excl')});pmsg.innerHTML='<div class="msg ok">Protocol saved (status: DRAFT)</div>';p_title.value=p_area.value=p_date.value=p_incl.value=p_excl.value="";loadProtocols();setTimeout(()=>toggleForm('protoForm'),700);}catch(e){pmsg.innerHTML='<div class="msg err">'+e.message+'</div>';}
}
async function registerSite(){
  if(!s_protocol.value){smsg.innerHTML='<div class="msg err">Protocol is mandatory.</div>';return;}
  const siteName = document.getElementById("s_name").value.trim();
    if (!siteName) {
      smsg.innerHTML='<div class="msg err">Site Name is mandatory.</div>';
      return;
    }
  try{await api("/api/protocols/sites","POST",{protocolId:+s_protocol.value,siteName:trimVal('s_name'),location:trimVal('s_location'),principalInvestigator:trimVal('s_pi')});smsg.innerHTML='<div class="msg ok">Site registered</div>';s_name.value=s_location.value=s_pi.value="";loadSites();setTimeout(()=>toggleForm('siteForm'),700);}catch(e){smsg.innerHTML='<div class="msg err">'+e.message+'</div>';}
}
async function activateSite(){try{await api("/api/protocols/sites/"+s_id.value+"/activate","PUT");sActMsg.innerHTML='<div class="msg ok">Site activated</div>';loadSites();}catch(e){sActMsg.innerHTML='<div class="msg err">'+e.message+'</div>';}}
async function setStatus(st){try{await api("/api/protocols/"+l_protocol.value+"/status?status="+st,"PUT");lmsg.innerHTML='<div class="msg ok">Status \u2192 '+st+'</div>';loadProtocols();}catch(e){lmsg.innerHTML='<div class="msg err">'+humanError(e,st)+'</div>';}}

function humanError(e,st){const m=e.message||"";return (m.includes("409")||m.includes("400"))?("Cannot move to "+st+" from the current status."):m;}

function updateLifecycleButtons(){
  const p=PROTOS.find(x=>x.protocolId===+l_protocol.value);
  const set=(id,on)=>{const b=document.getElementById(id);if(b)b.disabled=!on;};
  const info=document.getElementById("lifecycleInfo");
  if(!p){set("bApprove",0);set("bActivate",0);set("bClose",0);if(info)info.textContent="";return;}
  const s=p.protocolStatus;
  set("bApprove", s==="DRAFT");
  set("bActivate", s==="APPROVED");
  set("bClose", s==="APPROVED"||s==="ACTIVE");
  if(info)info.textContent="Current status: "+pretty(s);
}

function updateSiteButton(){
  const s=SITES.find(x=>x.siteId===+s_id.value);
  const b=document.getElementById("bActSite");
  if(!b)return;
  if(!s){b.disabled=true;return;}
  const p=PROTOS.find(x=>x.protocolId===s.protocolId);
  const ready=p&&(p.protocolStatus==="APPROVED"||p.protocolStatus==="ACTIVE");
  b.disabled=!(s.siteStatus==="REGISTERED" && ready);
}

function userTable(d,withActions=false){return d.length?`<table><tr><th>Id</th><th>Username</th><th>Name</th><th>Email</th><th>Role</th>${withActions?'<th>Action</th>':''}</tr>`+d.map(u=>`<tr><td><span class="ig-v mut">${u.id}</span></td><td><b>${u.username}</b></td><td>${u.fullName||"—"}</td><td>${u.email?`<a href="mailto:${u.email}">${u.email}</a>`:"—"}</td><td><span class="badge b-approved">${pretty(u.role)}</span></td>${withActions?`<td><button class="btn logout" onclick="deleteUser(${u.id})"><i class="bi bi-person-dash"></i> Delete User</button></td>`:''}</tr>`).join("")+"</table>":'<div class="empty">No users yet</div>';}
function protoTable(d){return d.length?`<table><tr><th>Id</th><th>Title</th><th>Phase</th><th>Status</th></tr>`+d.map(p=>`<tr><td><span class="ig-v mut">${p.protocolId}</span></td><td><b>${p.trialTitle}</b><br><small class="ig-v mut">${p.therapeuticArea||""}</small></td><td>${phaseLabel(p.phase)}</td><td>${badge(p.protocolStatus)}</td></tr>`).join("")+"</table>":'<div class="empty">No protocols yet</div>';}
function siteTable(d){return d.length?`<table><tr><th>Id</th><th>Protocol</th><th>Name</th><th>Location</th><th>PI</th><th>Status</th></tr>`+d.map(s=>`<tr><td><span class="ig-v mut">${s.siteId}</span></td><td>#${s.protocolId}</td><td><b>${s.siteName||""}</b></td><td>${s.location||"—"}</td><td>${s.principalInvestigator||"—"}</td><td>${badge(s.siteStatus)}</td></tr>`).join("")+"</table>":'<div class="empty">No sites yet</div>';}

async function loadUsers(){const d=await api("/users");USERS=d;utable.innerHTML=userTable(d,true);dUsers.innerHTML=userTable(d,false);cUsers.textContent=d.length;}
async function loadProtocols(){const d=await api("/api/protocols");PROTOS=d;ptable.innerHTML=protoTable(d);dProtocols.innerHTML=protoTable(d);cProtocols.textContent=d.length;cActive.textContent=d.filter(p=>p.protocolStatus==="ACTIVE").length;const eligible=d.filter(p=>p.protocolStatus==="APPROVED"||p.protocolStatus==="ACTIVE");fillSelect("s_protocol",eligible,"protocolId",protoLabel,"No approved protocols — approve one first");fillSelect("l_protocol",d,"protocolId",protoLabel,"No protocols yet");document.getElementById("s_btn").disabled=eligible.length===0;document.getElementById("addSiteBtn").disabled=eligible.length===0;updateLifecycleButtons();updateSiteButton();}
async function loadSites(){const d=await api("/api/protocols/sites");SITES=d;stable.innerHTML=siteTable(d);dSites.innerHTML=siteTable(d);cSites.textContent=d.length;fillSelect("s_id",d,"siteId",siteLabel,"No sites — register one first");updateSiteButton();}

loadUsers();loadProtocols();loadSites();



