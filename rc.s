/*
 * Generated Mon Jan 19 16:49:11 PST 2015
 */

	.section ".rodata"
	.align 4
_endl:	.asciz "\n"
_intFmt:	.asciz "%d"
_strFmt:	.asciz "%s"
_boolT:	.asciz "true"
_boolF:	.asciz "false"
arrrray_error:	.asciz "Index value of %d is outside legal range [0,%d).\n"
del_errrror:	.asciz "Attempt to dereference NULL pointer.\n"

	.section ".data"
	.align 4
.globalInit:	.word 0

! ---.MYS_foo---
	.section ".text"
	.align 4
	.global .MYS_foo
.MYS_foo:
	set			SAVE..MYS_foo, %g1
	save		%sp, %g1, %sp


!8
	set			8, %l0
	mov			%fp, %l2
	set			4, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!Referenced field x not found in type MYS. = 8
!Referenced field z not found in type MYS. = Referenced field y not found in type MYS.
	ret
	restore



	SAVE..MYS_foo = -(92 + 4) & -8
! ---.MYS_bar---
	.section ".text"
	.align 4
	.global .MYS_bar
.MYS_bar:
	set			SAVE..MYS_bar, %g1
	save		%sp, %g1, %sp


	ret
	restore



	SAVE..MYS_bar = -(92 + 0) & -8
! ---main---
	.section ".text"
	.align 4
	.global main
main:
	set			SAVE.main, %g1
	save		%sp, %g1, %sp

	set			.globalInit, %l0
	ld			[%l0], %l0
	cmp			%l0, %g0
	bne			.globalFinish
	nop

	set			.globalInit, %l0
	set			1, %l1
	st			%l1, [%l0]
.globalFinish:

!0
	set			0, %l0
	mov			%fp, %l2
	set			4, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

	mov			%fp, %l1
	set			4, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %i0
	ret
	restore


	ret
	restore



	SAVE.main = -(92 + 4) & -8
