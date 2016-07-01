# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing
VAGRANTFILE_API_VERSION = '2'

PLATFORM = "virtualbox"
BOX_NAME = "playa_mesos_ubuntu_14.04_201601041324"
BASE_URL = "http://downloads.mesosphere.io/playa-mesos"
IP_ADDRESS = "10.141.141.10"
VM_RAM = "2048"
VM_CPUS = "2"

ENV['VAGRANT_DEFAULT_PROVIDER'] = PLATFORM

BOX_URL = "#{BASE_URL}/#{BOX_NAME}-#{PLATFORM}.box"

# #############################################################################
# Vagrant VM Definitions
# #############################################################################

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  config.vm.network :private_network, ip: IP_ADDRESS

  # If true, then any SSH connections made will enable agent forwarding.
  # Default value: false
  config.ssh.forward_agent = true

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "playa_mesos_ubuntu_14.04_201606060830"

  config.vm.box_url = BOX_URL

  # Note: You'll want a decent amount of memory for your mesos master/slave
  # VM. The strict minimum, at least while the VM is provisioned, is the
  # amount necessary to compile mesos and the jenkins plugin. 2048m+ is
  # recommended.  The CPU count can be lowered, but you may run into issues
  # running the Jenkins Mesos Framework if you do so.
  config.vm.provider :virtualbox do |vb|
    vb.name = BOX_NAME
    vb.customize ['modifyvm', :id, '--memory', VM_RAM]
    vb.customize ['modifyvm', :id, '--cpus',   VM_CPUS]
  end

  # Make the project root available to the guest VM.
  # config.vm.synced_folder '.', '/vagrant'

end
