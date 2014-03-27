import json, os, pprint, requests, re, shutil, sys
from subprocess import call

boilerplate = '''#extension GL_OES_standard_derivatives : enable
precision highp float;

uniform vec3      iResolution;           // viewport resolution (in pixels)
uniform float     iGlobalTime;           // shader playback time (in seconds)
uniform float     iChannelTime[4];       // channel playback time (in seconds)
uniform vec3      iChannelResolution[4]; // channel resolution (in pixels)
uniform vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click
uniform vec4      iDate;                 // (year, month, day, time in seconds)
'''

sid = sys.argv[1]
if '/' in sid:
	sid = sid.split('/')[-1]

blob = json.dumps({'shaders' : [sid]})
r = requests.post('https://www.shadertoy.com/shadertoy', data=dict(s=blob), headers=dict(Referer='https://www.shadertoy.com/view/' + sid))
shader = r.json()[0]
info = shader['info']
render = shader['renderpass'][0]
#pprint.pprint(info)
#pprint.pprint(render)
username = info['username']
if username[0] not in 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_':
	username = '_' + username
username = re.sub(r'[^A-Za-z0-9_]', '_', username)
sname = re.sub(r'[^A-Za-z0-9_]', '_', info['name'])

try:
	shutil.rmtree('build')
except:
	pass
shutil.copytree('template', 'build')

for elem in render['inputs']:
	print elem
	if elem['ctype'] == 'texture':
		boilerplate += 'uniform sampler2D iChannel%i;\n' % elem['channel']
	elif elem['ctype'] == 'music':
		boilerplate += 'uniform sampler2D iChannel%i;\n' % elem['channel']
	elif elem['ctype'] == 'cubemap':
		boilerplate += 'uniform samplerCube iChannel%i;\n' % elem['channel']
	else:
		print 'unhandled input', elem

#print boilerplate

code = render['code']

with file('build/res/raw/fs.glsl', 'w') as fp:
	fp.write(boilerplate + '\n' + code)

def rewrite(fn):
	contents = file(fn, 'r').read()
	contents = contents.replace('%USER%', username)
	contents = contents.replace('%NAME%', info['name'])
	contents = contents.replace('%SNAME%', sname)
	file(fn, 'w').write(contents)

rewrite('build/pom.xml')
rewrite('build/AndroidManifest.xml')
rewrite('build/src/main/java/com/shaderunner/user/MainActivity.java')
rewrite('build/src/main/java/com/shaderunner/user/ShaderRenderer.java')

os.rename('build/src/main/java/com/shaderunner/user', 'build/src/main/java/com/shaderunner/%s' % username)

os.chdir('build')
call(['mvn', 'package'])
call(['adb', '-s', '0514713800008105D97B', 'install', '-r', 'target/%s.apk' % sname])
call(['adb', '-s', '0514713800008105D97B', 'shell', 'am', 'start', '-n', 'com.shaderunner.%s/com.shaderunner.%s.MainActivity' % (username, username)])
